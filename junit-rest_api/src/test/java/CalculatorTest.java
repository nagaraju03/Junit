import com.rest.app.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rest.app.Book;
import com.rest.app.BookController;
import com.rest.app.BookRepo;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CalculatorTest {

    Calculator calculator;
    @BeforeEach
    public void setUp(){
        calculator=new Calculator();
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testMultiply(){
        assertEquals(4,calculator.multiply(2,2));
        assertEquals(22,calculator.multiply(2,11));
    }

    @Test
    public void testDivide(){
        assertEquals(5,calculator.divide(20,4));
    }


    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private BookRepo bookRepo;

    @InjectMocks
    private BookController bookController;

    Book RECORD_1 = new Book(1L, "HP", "A", 3);
    Book RECORD_2 = new Book(2L, "DELL", "B", 4);
    Book RECORD_3 = new Book(3L, "Apple", "C", 5);
//    Book RECORD_4 = new Book(null, "", "", "");


    @Test
    public void getAllRecords_Success() throws Exception{
        List<Book> records = new ArrayList<>(Arrays.asList(RECORD_1,RECORD_2,RECORD_3));

        Mockito.when(bookRepo.findAll()).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].name", is("Apple")))
                .andExpect(jsonPath("$[1].name", is("DELL")));

    }

    @Test
    public void getBookById_Success() throws Exception{
        Mockito.when(bookRepo.findById(RECORD_1.getBookId())).thenReturn(Optional.of(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("HP")));
    }

    @Test
    public void getBookById_NotFound() {

        Mockito.when(bookRepo.findById(7L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> mockMvc.perform(MockMvcRequestBuilders
                .get("/book/7")
                .contentType(MediaType.APPLICATION_JSON)));

        String expectedMessage = "Book Id 7 not found";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void createRecord_Success() throws Exception {

//        Book record = Book.builder().BookId(4L).name("Asus").summary("D").rating(2).build();

        Mockito.when(bookRepo.save(RECORD_1)).thenReturn(RECORD_1);

        String content = objectWriter.writeValueAsString(RECORD_1);

        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockReq)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("HP")));

    }

    @Test
    public void updateBookRecord_Success() throws Exception {

        Book updatedRecord = Book.builder().BookId(1L).name("new").summary("updated summary").rating(3).build();

        Mockito.when(bookRepo.findById(RECORD_1.getBookId())).thenReturn(Optional.ofNullable(RECORD_1));
        Mockito.when(bookRepo.save(updatedRecord)).thenReturn(updatedRecord);

        String content = objectWriter.writeValueAsString(updatedRecord);

        MockHttpServletRequestBuilder mockReq = MockMvcRequestBuilders.put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockReq)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.name",is("new")))
                .andExpect(jsonPath("$.summary",is("updated summary")));

    }

    @Test
    public void deleteBookById_Success() throws Exception {

        Mockito.when(bookRepo.findById(RECORD_2.getBookId())).thenReturn(Optional.ofNullable(RECORD_2));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/book/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void deleteBookById_NotFound(){
        Mockito.when(bookRepo.findById(7L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> mockMvc.perform(MockMvcRequestBuilders
                .delete("/book/7")
                .contentType(MediaType.APPLICATION_JSON)));

        String expectedMessage = "Book Id 7 not found";
        String actualMessage = exception.getMessage();
        System.out.println(actualMessage);
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
