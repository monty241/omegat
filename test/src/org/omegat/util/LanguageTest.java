/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2016 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
**************************************************************************/

package org.omegat.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

/**
 * Tests for OmegaT language handling.
 *
 * @author Maxym Mykhalchuk
 */
public class LanguageTest {

    /**
     * Test of getLanguage method, of class org.omegat.util.Language.
     *
     * @see <a href="https://sourceforge.net/p/omegat/bugs/185/">bug #185</a>
     */
    @Test
    public void testGetLanguage() {
        String lang1 = "xx-YY";
        assertEquals(lang1, new Language(lang1).getLanguage());

        // Obsolete, the language (Locale) is always stored as lc-UC
        // String lang2 = "XX-yy";
        // assertEquals(lang2, new Language(lang2).getLanguage());
    }

    /**
     * Test of getLocale method, of class org.omegat.util.Language.
     */
    @Test
    public void testGetLocale() {
        String lang = "XXX-yy";
        assertEquals("xxx_YY", new Language(lang).getLocaleCode());
    }

    /**
     * Test of equals method, of class org.omegat.util.Language.
     */
    @Test
    public void testEquals() {
        Language lang1 = new Language("xxx-YY");
        Language lang2 = new Language("XXX-yy");
        Language lang3 = new Language("xxx_YY");
        assertEquals(lang1, lang2);
        assertEquals(lang1, lang3);
        assertEquals(lang2, lang3);
    }

    /**
     * Test the constructor under lots of stress.
     */
    @Test
    public void testConstructor() {
        Language lang1 = new Language((Locale) null);
        Language lang2 = new Language((String) null);
        assertEquals(lang1, lang2);
    }

    /**
     * Test for BCP 47 language tags.
     */
    @Test
    public void testBCP47() {
        Language lang1 = new Language("en-KW-x-ukeng");
        Language lang2 = new Language("en-KW-x-useng");
        Language lang2b = new Language("EN-KW-W-UsENg");
        assertEquals(lang1.getLanguageCode(), lang2.getLanguageCode());
        assertEquals(lang2.getLanguageCode(), lang2b.getLanguageCode());

        assertFalse(Language.verifySingleLangCode("xxx+ZZZ-a-BBB-ccc"));
        assertTrue(Language.verifySingleLangCode("es-419"));
        assertTrue(Language.verifySingleLangCode("ar-AE-x-dubai"));
    }
}
