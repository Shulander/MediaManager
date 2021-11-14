package us.vicentini.mediamanager.filefilter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class SeriesFileFilterTest {

    @Test
    void shouldMatchSeriesPatterns() {
        Pattern p = Pattern.compile("(.*?)[.\\s][sS](\\d{2})[eE](\\d{2})(.*)");

        String[] tests = {"xyz title name S01E02 bla bla",
                          "bla bla title name.S03E04",
                          "the season title name s05e03",
                          "Big Hero   6 3D S01E03 2014 1080p BRRip Half-OU x264 AC3-JYK"};

        for (String s : tests) {
            Matcher m = p.matcher(s);
            assertTrue(m.matches());
            log.info(String.format("Name: %-23s Season: %s Episode: %s [%s]",
                                   m.group(1), m.group(2), m.group(3), m.group(4)));
        }

    }


    @Test
    void shouldSplitSeparators() {
        String[] tokens = "Big.Hero 6".split("(\\.|\\s)");

        assertNotNull(tokens);
        assertEquals(3, tokens.length);

        for (String split : tokens) {
            log.info(split);
        }
    }
}
