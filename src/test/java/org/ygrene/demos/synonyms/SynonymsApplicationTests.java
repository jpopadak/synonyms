package org.ygrene.demos.synonyms;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SynonymsApplicationTests {

    @Test
    public void contextLoads() {
        // This class and test ensures that the normal spring context for the
        // application loads with all valid dependencies and beans setup.
        assertThat(true, equalTo(true));
    }

}
