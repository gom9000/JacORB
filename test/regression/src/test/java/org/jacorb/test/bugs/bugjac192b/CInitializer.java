package org.jacorb.test.bugs.bugjac192b;

import org.omg.CORBA.INTERNAL;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

/**
 * <code>CInitializer</code> is basic initializer to register the interceptor.
 *
 * @author Nick Cross
 */
public class CInitializer
    extends org.omg.CORBA.LocalObject
    implements ORBInitializer
{
    /**
     * This method registers the interceptors.
     * @param info an <code>ORBInitInfo</code> value
     */
    public void post_init( ORBInitInfo info )
    {
    }

    /**
     * <code>pre_init</code> does nothing..
     *
     * @param info an <code>ORBInitInfo</code> value
     */
    public void pre_init(ORBInitInfo info)
    {
        try
        {
            info.add_client_request_interceptor(new AInterceptor());
        }
        catch (DuplicateName e)
        {
            throw new INTERNAL();
        }
    }
}