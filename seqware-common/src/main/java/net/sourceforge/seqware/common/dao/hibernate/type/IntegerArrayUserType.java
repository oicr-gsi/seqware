/*
 * Copyright (C) 2015 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.dao.hibernate.type;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 *
 * @author mlaszloffy
 */
public class IntegerArrayUserType implements UserType {

    protected static final int SQLTYPE = java.sql.Types.ARRAY;

    @Override
    public int[] sqlTypes() {
        return new int[]{SQLTYPE};
    }

    @Override
    public Class returnedClass() {
        return Integer[].class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        return o == null ? o1 == null : o.equals(o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] strings, Object o) throws HibernateException, SQLException {
        Array array = rs.getArray(strings[0]);
        if (array != null) {
            return (Integer[]) array.getArray();
        } else {
            return new Integer[0];
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement ps, Object o, int i) throws HibernateException, SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return o == null ? null : ((String[]) o).clone();
    }

    @Override
    public boolean isMutable() {
        return false;

    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return (Serializable) o;

    }

    @Override
    public Object assemble(Serializable srlzbl, Object o) throws HibernateException {
        return srlzbl;

    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return o;

    }

}
