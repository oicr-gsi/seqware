/*
 * Copyright (C) 2016 SeqWare
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
package net.sourceforge.seqware.common.dto;

import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import net.sourceforge.seqware.common.model.LimsKey;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author mlaszloffy
 */
public class IusLimsKeyDto implements IusLimsKey {

    private Integer iusSWID;
    private LimsKey limsKey;

    @Override
    public Integer getIusSWID() {
        return iusSWID;
    }

    public void setIusSWID(Integer iusSWID) {
        this.iusSWID = iusSWID;
    }

    @Override
    public LimsKey getLimsKey() {
        return limsKey;
    }

    public void setLimsKey(LimsKey limsKey) {
        this.limsKey = limsKey;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IusLimsKeyDto other = (IusLimsKeyDto) obj;
        return EqualsBuilder.reflectionEquals(other, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
