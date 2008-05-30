/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
           (C) 2005-06 Henry Pijffers
           (C) 2006 Martin Wunderlich
           (C) 2006-2007 Didier Briel
           (C) 2008 Martin Fleurke
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
**************************************************************************/

package org.omegat.core.data;

import java.util.ArrayList;
import java.util.List;

import org.omegat.core.Core;
import org.omegat.core.segmentation.Rule;
import org.omegat.core.segmentation.Segmenter;
import org.omegat.filters2.IParseCallback;
import org.omegat.util.Language;
import org.omegat.util.StaticUtils;

/**
 * Process one entry on parse source file.
 * 
 * @author Maxym Mykhalchuk
 * @author Henry Pijffers
 */
public abstract class ParseEntry implements IParseCallback {
    /**
     * This method is called by filters to:
     * <ul>
     * <li>Instruct OmegaT what source strings are translatable.
     * <li>Get the translation of each source string.
     * </ul>
     * 
     * @param entry
     *                Translatable source string
     * @return Translation of the source string. If there's no translation,
     *         returns the source string itself.
     */
    public String processEntry(String entry) {
        // replacing all occurrences of single CR (\r) or CRLF (\r\n) by LF (\n)
        // this is reversed at the end of the method
        // fix for bug 1462566
        boolean crlf = entry.indexOf("\r\n") > 0;
        if (crlf)
            entry = entry.replaceAll("\\r\\n", "\n");
        boolean cr = entry.indexOf("\r") > 0;
        if (cr)
            entry = entry.replaceAll("\\r", "\n");

        // Some special space handling: skip leading and trailing whitespace
        // and non-breaking-space
        int len = entry.length();
        int b = 0;
        StringBuffer bs = new StringBuffer();
        while (b < len
                && (Character.isWhitespace(entry.charAt(b)) || entry.charAt(b) == '\u00A0')) {
            bs.append(entry.charAt(b));
            b++;
        }

        int e = len - 1;
        StringBuffer es = new StringBuffer();
        while (e >= b
                && (Character.isWhitespace(entry.charAt(e)) || entry.charAt(e) == '\u00A0')) {
            es.append(entry.charAt(e));
            e--;
        }
        es.reverse();

        entry = StaticUtils.fixChars(entry.substring(b, e + 1));

        StringBuffer res = new StringBuffer();
        res.append(bs);

        if (Core.getDataEngine().getProjectProperties()
                .isSentenceSegmentingEnabled()) {
            List<StringBuffer> spaces = new ArrayList<StringBuffer>();
            List<Rule> brules = new ArrayList<Rule>();
            Language sourceLang = Core.getDataEngine().getProjectProperties()
                    .getSourceLanguage();
            Language targetLang = Core.getDataEngine().getProjectProperties()
                    .getSourceLanguage();
            List<String> segments = Segmenter.segment(sourceLang, entry,
                    spaces, brules);
            for (int i = 0; i < segments.size(); i++) {
                String onesrc = segments.get(i);
                segments.set(i, processSingleEntry(onesrc));
            }
            res.append(Segmenter.glue(sourceLang, targetLang, segments, spaces,
                    brules));
        } else
            res.append(processSingleEntry(entry));

        res.append(es);

        // replacing all occurrences of LF (\n) by either single CR (\r) or CRLF
        // (\r\n)
        // this is a reversal of the process at the beginning of this method
        // fix for bug 1462566
        String result = res.toString();
        if (crlf)
            result = result.replaceAll("\\n", "\r\n");
        else if (cr)
            result = result.replaceAll("\\n", "\r");

        return result;
    }

    protected abstract String processSingleEntry(String src);
}
