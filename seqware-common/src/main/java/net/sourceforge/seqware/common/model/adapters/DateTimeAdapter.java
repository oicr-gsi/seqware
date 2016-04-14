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
package net.sourceforge.seqware.common.model.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author mlaszloffy
 */
public class DateTimeAdapter extends XmlAdapter<String, DateTime> {

    @Override
    public DateTime unmarshal(String date) throws Exception {
        return DateTime.parse(date).toDateTime(DateTimeZone.UTC);
    }

    @Override
    public String marshal(DateTime date) throws Exception {
        return date.toString();
    }

}
