package org.jacorb.security.sas;

/*
 *        JacORB - a free Java ORB
 *
 *   Copyright (C) 2002-2002 Gerald Brose
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Library General Public
 *   License as published by the Free Software Foundation; either
 *   version 2 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this library; if not, write to the Free
 *   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

//import org.omg.PortableInterceptor.*;
//import org.omg.PortableInterceptor.ORBInitInfoPackage.*;
//import org.omg.SecurityReplaceable.*;
import org.omg.Security.*;
//import org.omg.IOP.*;
//import org.omg.IOP.CodecFactoryPackage.*;
//import org.jacorb.util.*;
import org.omg.SecurityLevel2.*;

//import org.jacorb.util.Environment;
import org.omg.PortableInterceptor.*;
import java.security.cert.X509Certificate;

import org.jacorb.util.*;
import org.jacorb.orb.portableInterceptor.ServerRequestInfoImpl;
import org.jacorb.security.level2.*;
import org.jacorb.orb.dsi.ServerRequest;
import org.jacorb.orb.connection.*;

import javax.net.ssl.SSLSocket;

public class JsseContextValidator implements ISASContextValidator
{
    private X509Certificate client_cert = null;

    public boolean validate(ServerRequestInfo ri) {
        client_cert = getClientCert(ri);
        if (client_cert == null) return false;
        return true;
    }

    public String getPrincipalName() {
        if (client_cert == null) return null;
        return client_cert.getSubjectDN().getName();
    }

    /**
     * This method retrievs the received client certificate
     * from the Credentials.
     */
    private X509Certificate getClientCert(ServerRequestInfo ri)
    {
        ServerRequest request = ((ServerRequestInfoImpl) ri).request;

        GIOPConnection connection = request.getConnection();

        // lookup for context
        if (connection == null)
        {
            Debug.output( 3, "target has no connection!");
            return null;
        }

        if( !connection.isSSL() )
        {
            return null;
        }

        Server_TCP_IP_Transport transport =
            (Server_TCP_IP_Transport) connection.getTransport();

        SSLSocket sslSocket = (SSLSocket) transport.getSocket();
        try
        {
            return (X509Certificate)sslSocket.getSession().getPeerCertificates()[0];
        }
        catch( javax.net.ssl.SSLPeerUnverifiedException pue )
        {
            Debug.output( 2, pue );
            return null;
        }

        /*

        KeyAndCert kac = null;

        try
        {
            kac =
                new KeyAndCert( null,  sslSocket.getSession().getPeerCertificates() );
        }
        catch( javax.net.ssl.SSLPeerUnverifiedException pue )
        {
            Debug.output( 2, pue );
            return;
        }

        if( kac.chain == null )
        {
            Debug.output( 2, "Client sent no certificate chain!" );

            return;
        }

        SecAttribute [] atts = new SecAttribute[] {
            attrib_mgr.createAttribute( kac, type ) } ;

        current.set_received_credentials( new ReceivedCredentialsImpl( atts ) );




        SecAttributeManager attrib_mgr = SecAttributeManager.getInstance();

        AttributeType attribute_type =
            new AttributeType(new ExtensibleFamily((short) 0,
                                                   (short) 1),
                              AccessId.value);

        AttributeType[] access_id = new AttributeType[] {attribute_type};

        org.omg.SecurityLevel2.Current current = null;
        try {
          current = (org.omg.SecurityLevel2.Current)orb.resolve_initial_references( "SecurityCurrent" );
        } catch (Exception e) {
            Debug.output(1, "Error getting current: " + e);
            return null;
        }

        //get the ReceivedCredentials
        ReceivedCredentials creds = current.received_credentials();

        if (creds == null)
        {
            System.out.println("No received credentials in Current");
            return null;
        }

        //get the SecAttributes we're interested in
        SecAttribute[] attribs = creds.get_attributes( access_id );

        if( attribs.length == 0 )
        {
            System.out.println("No attributes in Current credentials");
            return null;
        }

        //get the actual contents of the SecAttributes via
        //the SecAttributeManager
        KeyAndCert kac = attrib_mgr.getAttributeCertValue( attribs[0] );

        if( kac == null )
        {
            System.out.println("Could not get Cert Attribute Value for "+attribs[0]);
            return null;
        }

        //return the first (self-signed) certificate of the chain
        return (X509Certificate) kac.chain[0];
        */
    }
}
