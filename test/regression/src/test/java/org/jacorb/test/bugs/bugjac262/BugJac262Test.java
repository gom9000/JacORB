package org.jacorb.test.bugs.bugjac262;

/*
 *        JacORB  - a free Java ORB
 *
 *   Copyright (C) 1997-2014 Gerald Brose / The JacORB Team.
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

import org.jacorb.test.harness.ClientServerSetup;
import org.jacorb.test.harness.ClientServerTestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BugJac262Test extends ClientServerTestCase
{
    private ComplexTypeCodesServer server;

    @Before
    public void setUp() throws Exception
    {
        server = ComplexTypeCodesServerHelper.narrow( setup.getServerObject() );
    }

    @BeforeClass
    public static void beforeClassSetUp() throws Exception
    {
        setup = new ClientServerSetup( ComplexTypeCodesServerImpl.class.getName() );
    }

    @Test
    public void test_mixed_typecodes()
    {
        org.omg.CORBA.Any any = setup.getClientOrb().create_any();

        any.insert_TypeCode(MixedStructsHelper.type());
        server.passAny( any );
    }

    @Test
    public void test_repeated_sequence_typecodes()
    {
        org.omg.CORBA.Any any = setup.getClientOrb().create_any();

        any.insert_TypeCode(RepeatedSeqStructHelper.type());
        server.passAny(any);
    }

    @Test
    public void test_repeated_typecodes()
    {
        org.omg.CORBA.Any any = setup.getClientOrb().create_any();

        any.insert_TypeCode(RepeatedStructHelper.type());

        server.passAny(any);
    }

    @Test
    public void test_array_typecodes()
    {
        org.omg.CORBA.Any any = setup.getClientOrb().create_any();

        any.insert_TypeCode(RepeatedArrayStructHelper.type());
        server.passAny(any);
    }

    @Test
    public void test_object_typecodes()
    {
        org.omg.CORBA.Any any = setup.getClientOrb().create_any();

        any.insert_TypeCode(RepeatedObjectStructHelper.type());
        server.passAny(any);
    }
}
