package com.testing123;

import com.thirdpartylib.StringService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class GreeterServiceTest {

    @Test
    /*
    Pros: it does find the bug, simple to write
    Cons: it doesn't isolate GreeterService from its dependencies

    Test fixture      /-----------------------------------------------\
                      |                             /-------------\   |
      test            |    /-----------------\  +-->|StringService|   |
      method-------------->|GreeterService   |--+   \-------------/   |
                      |    |(unit under test)|---+                    |
                      |    \-----------------/   |  /---------------\ |
                      |                          +->|Java runtime/  | |
                      |                             |utility classes| |
                      |(environment)                \---------------/ |
                      \-----------------------------------------------/
     */
    void makeGreeting_actualStringService() {
        GreeterService greeterService = new GreeterService();

        String result = greeterService.makeGreeting("Fred");

        Assertions.assertThat(result).isEqualTo("Hello, Fred :)");
    }

    @Test
    /*
    Pros: you have test coverage
    Cons: you missed the bug

    Test fixture      /-----------------------------------------------\
                      |                                               |
      test            |    /-----------------\                        |
      method-------------->|GreeterService   |                        |
                      |    |(unit under test)|---+                    |
                      |    \-----------------/   |  /---------------\ |
      mock            |        |                 +->|Java runtime/  | |
      StringService<-----------+                    |utility classes| |
                      |(environment)                \---------------/ |
                      \-----------------------------------------------/
     */
    void makeGreeting_mockStringService_relaxed1() {
        GreeterService greeterService = new GreeterService();
        StringService mockStringService = Mockito.mock(StringService.class);
        greeterService.setStringService(mockStringService);
        Mockito.when(mockStringService.contatenateStrings(any(), any())).thenReturn("Hello, Fred");

        String result = greeterService.makeGreeting("Fred");

        Assertions.assertThat(result).isEqualTo("Hello, Fred :)");
    }

    @Test
    /*
    Pros: it does find the bug
    Cons: the location of the problem is not clear

    Test fixture      /-----------------------------------------------\
                      |                                               |
      test            |    /-----------------\                        |
      method-------------->|GreeterService   |                        |
                      |    |(unit under test)|---+                    |
                      |    \-----------------/   |  /---------------\ |
      mock            |        |                 +->|Java runtime/  | |
      StringService<-----------+                    |utility classes| |
                      |(environment)                \---------------/ |
                      \-----------------------------------------------/

     */
    void makeGreeting_mockStringService_strict() {
        GreeterService greeterService = new GreeterService();
        StringService mockStringService = Mockito.mock(StringService.class);
        greeterService.setStringService(mockStringService);
        Mockito.when(mockStringService.contatenateStrings("Hello, ", "Fred")).thenReturn("Hello, Fred");

        String result = greeterService.makeGreeting("Fred");

        Assertions.assertThat(result).isEqualTo("Hello, Fred :)");
    }


    /*
    Pros: The source of the problem is clear
    Cons: The test is a bit more complex to write
     */
    @Test
    void makeGreeting_mockStringService_relaxed2() {
        GreeterService greeterService = new GreeterService();
        StringService mockStringService = Mockito.mock(StringService.class);
        greeterService.setStringService(mockStringService);
        Mockito.when(mockStringService.contatenateStrings(any(), any())).thenReturn("Hello, Fred");

        String result = greeterService.makeGreeting("Fred");

        Assertions.assertThat(result).isEqualTo("Hello, Fred :)");
        Mockito.verify(mockStringService, Mockito.times(1)).contatenateStrings("Hello, ", "Fred");
    }

    /*
    Pros: Easier to validate part of a complex argument without having to build it
    Cons: Error message is not quite as clear
     */

    @Test
    void makeGreeting_mockStringService_captors() {
        GreeterService greeterService = new GreeterService();
        StringService mockStringService = Mockito.mock(StringService.class);
        greeterService.setStringService(mockStringService);

        ArgumentCaptor<String> s1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> s2 = ArgumentCaptor.forClass(String.class);
        Mockito.when(mockStringService.contatenateStrings(s1.capture(), s2.capture())).thenReturn("Hello, Fred");

        String result = greeterService.makeGreeting("Fred");

        Assertions.assertThat(result).isEqualTo("Hello, Fred :)");

        Assertions.assertThat(s1.getValue()).isEqualTo("Hello, ");
        Assertions.assertThat(s2.getValue()).isEqualTo("Fred");
    }

}
