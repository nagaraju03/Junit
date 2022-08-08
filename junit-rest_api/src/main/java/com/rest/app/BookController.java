package com.rest.app;

//import jakarta.validation.Valid;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/book")
public class BookController {

    @Autowired
    BookRepo bookRepo;

    @GetMapping
    public List<Book> getAllBookRecords(){
        return bookRepo.findAll();
    }

    @GetMapping(value = "{bookId}")
    public Book getBookById(@PathVariable(value= "bookId") Long bookId) throws Exception{
        if(bookRepo.findById(bookId).isEmpty()) {
            throw new Exception("Book Id " + bookId + " not found");
        }
            return bookRepo.findById(bookId).get();
    }

    @PostMapping
    public Book createBookRecord(@RequestBody @Valid Book bookRecord){
        return bookRepo.save(bookRecord);
    }

    @PutMapping
    public Book updateBookRecord(@RequestBody @Valid Book bookRecord) throws NotFoundException {
        if(bookRecord == null || bookRecord.getBookId() == null){
            throw new NotFoundException("BookRecord must not be null");
        }
        Optional<Book> optionalBook = bookRepo.findById(bookRecord.getBookId());
        if(optionalBook.isEmpty()){
            throw new NotFoundException("Book with ID" + bookRecord.getBookId() + "Not Exist");
        }
        Book existingBookRecord = optionalBook.get();
        existingBookRecord.setName(bookRecord.getName());
        existingBookRecord.setSummary(bookRecord.getSummary());
        existingBookRecord.setRating(bookRecord.getRating());
        return bookRepo.save(existingBookRecord);
    }

    @DeleteMapping(value = "{bookId}")
    public void deleteBookById(@PathVariable(value = "bookId") Long bookId) throws NotFoundException {

        if(bookRepo.findById(bookId).isEmpty()){
            throw new NotFoundException("Book Id " + bookId + " not found");
        }
        bookRepo.deleteById(bookId);
    }

}
