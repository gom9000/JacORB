package org.jacorb.security.level2;

import java.io.*;
import java.net.*;
import java.util.*;

import java.security.*;
import java.security.cert.*;

import org.omg.SecurityLevel2.*;
import org.omg.Security.*;

import org.jacorb.util.*;
import org.jacorb.security.util.*;

/**
 * PrincipalAuthenticatorImpl
 * 
 * This simple authenticator just retrieves X.509v3 certificates
 * from a Java key store
 *
 * @author Gerald Brose
 * $Id$
 */

public class PrincipalAuthenticatorImpl
    extends org.jacorb.orb.LocalityConstrainedObject
    implements org.omg.SecurityLevel2.PrincipalAuthenticator
{  
    private LoginData loginData = null;
    private KeyStore keyStore = null;

    private SecAttributeManager attrib_mgr = null;
    
    // rt: orb param removed (this simplyfies the using with java reflection)

    public PrincipalAuthenticatorImpl()
    {
        loginData = new LoginData();
    	loginData.keyStoreLocation = 
            Environment.getProperty( "jacorb.security.keystore" );
        loginData.storePassphrase = 
            Environment.getProperty("jacorb.security.keystore_password");
        attrib_mgr = SecAttributeManager.getInstance();
    }  

    public int[] get_supported_authen_methods(java.lang.String mechanism)
    {
	return new int[]{0};
    }

    public AuthenticationStatus authenticate(int method, 
                                             String mechanism, 
                                             String security_name, //user name
                                             byte[] auth_data, //  passwd
                                             SecAttribute[] privileges, 
                                             CredentialsHolder creds, 
                                             OpaqueHolder continuation_data, 
                                             OpaqueHolder auth_specific_data
                                             )
    {
	Debug.output( 3,"starting authentication" );
	try 
	{	
	    registerProvider();

            loginData.alias = security_name;

            if ( auth_data == null )
            {
                loginData.password = null;
            }
            else
            {
                loginData.password = new String( auth_data );
            }

            if ( loginData.keyStoreLocation == null ) 
            {
                System.out.print("Please enter key store file name: ");
                loginData.keyStoreLocation = (new BufferedReader(new InputStreamReader(System.in))).readLine();
            }

            if ( loginData.storePassphrase == null ) 
            {
                System.out.print("Please enter store pass phrase: ");
                loginData.storePassphrase = (new BufferedReader(new InputStreamReader(System.in))).readLine();
            }

            if ( loginData.alias == null ) 
            {
                System.out.print("Please enter alias  name: ");
                loginData.alias = (new BufferedReader(new InputStreamReader(System.in))).readLine();
            }

            if ( loginData.password == null ) 
            {
                System.out.print("Please enter password: ");
                loginData.password = (new BufferedReader(new InputStreamReader(System.in))).readLine();
            }
              

            if (( loginData.keyStoreLocation == null ) || 
                ( loginData.storePassphrase == null ) ||
                ( loginData.alias == null ) || 
                ( loginData.password == null ))
            {
                return AuthenticationStatus.SecAuthFailure;
            }

            keyStore = KeyStoreUtil.getKeyStore (loginData.keyStoreLocation, 
                                                 loginData.storePassphrase.toCharArray());

            X509Certificate[] cert_chain = (X509Certificate[]) 
                keyStore.getCertificateChain( loginData.alias );

            if( cert_chain == null )
            {
                Debug.output( 0, "No keys found in keystore for alias \""+
                              loginData.alias + "\"!" );

                if( Environment.getProperty( "jacorb.security.default_user" ) != null )
                {
                    Debug.output( 0, "Please check property \"jacorb.security.default_user\"" );
                }
            
                return org.omg.Security.AuthenticationStatus.SecAuthFailure;
            }
            
            PrivateKey priv_key = (PrivateKey) 
                keyStore.getKey ( loginData.alias, 
                                  loginData.password.toCharArray() );


            KeyAndCert k_a_c = new KeyAndCert( priv_key, cert_chain );

            AttributeType type = new AttributeType
                ( new ExtensibleFamily( (short) 0,
                                        (short) 1 ),
                  AccessId.value );


            SecAttribute attrib = attrib_mgr.createAttribute( k_a_c,
                                                              type );
                
            CredentialsImpl credsImpl = 
                new CredentialsImpl( new SecAttribute[]{ attrib },
                AuthenticationStatus.SecAuthSuccess,
                InvocationCredentialsType.SecOwnCredentials);

            /*
            credsImpl.accepting_options_supported( (short) Environment.getIntProperty( "jacorb.security.ssl.client.supported_options", 16 ));

            credsImpl.accepting_options_required( (short) Environment.getIntProperty( "jacorb.security.ssl.client.required_options", 16 ));

            credsImpl.invocation_options_supported( (short) Environment.getIntProperty( "jacorb.security.ssl.client.supported_options", 16 ));

            credsImpl.invocation_options_required( (short) Environment.getIntProperty( "jacorb.security.ssl.client.required_options", 16 ));
            */
            
            creds.value = credsImpl;

            Debug.output(3,"authentication succeeded");

            return AuthenticationStatus.SecAuthSuccess;
	}
	catch (Exception e) 
	{
	    Debug.output(2,e);

	    return org.omg.Security.AuthenticationStatus.SecAuthFailure;
	}
    }

    /** 
     * not implemented
     */
  
    public AuthenticationStatus continue_authentication(byte[] response_data, 
							Credentials creds, 
							OpaqueHolder continuation_data, 
							OpaqueHolder auth_specific_data)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }


    private void registerProvider()
    {
        iaik.security.provider.IAIK.addAsProvider();

        Debug.output(3, "added Provider IAIK" );
    }
}










