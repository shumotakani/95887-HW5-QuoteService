package edu.shuwang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import edu.shuwang.form.QuoteForm;
import edu.shuwang.model.Author;
import edu.shuwang.model.Quote;
import edu.shuwang.service.QuoteService;

@Service
public class QuoteAction {
	
	@Value("${service.author.name.uri}")
    private String authorServerNameUri;
    @Value("${service.author.id.uri}")
    private String authorServerIdUri;
    @Value("${service.author.save.uri}")
    private String authorServerSaveUri;
    
    @Autowired
    private QuoteService quoteService;
    
    @HystrixCommand(fallbackMethod = "randomFallback")
    public Quote random() {
        System.out.println("QC random() is running");
        return quoteService.randomQuote();
    }
    
    @HystrixCommand(fallbackMethod = "listFallback")
    public Quote[] list(String authorName) {
        //pass the author name into the Controller use CRUD to solve the issue
        System.out.println("You are looking for quotes from " + authorName);
        
        RestTemplate restTemplate = new RestTemplate();
        String uri = authorServerNameUri + authorName;
        Author author = restTemplate.getForObject(uri, Author.class);
        
        return quoteService.findByAuthorId(author.getId());
    }
    
    public void saveQuote(QuoteForm quoteForm) {
    	RestTemplate restTemplate = new RestTemplate();
        String uri = authorServerNameUri+quoteForm.getAuthorName();
        Author a = restTemplate.getForObject(uri, Author.class);
        
        if (a == null) {
            System.out.println("----------Saving as a !!!NEW!!! author----------");
            System.out.println(quoteForm.getAuthorName());
            ResponseEntity<Long> st = restTemplate.postForEntity(authorServerSaveUri, quoteForm.getAuthorName(), Long.class);
            System.out.println(st.getBody());
            Quote quote = new Quote(quoteForm.getText(), quoteForm.getSource(), st.getBody().longValue());
            System.out.println("Saving Quote");
            quoteService.save(quote);
            return;
        } else {
            System.out.println("----------Saving as a !!!OLD!!! author----------");
            Quote quote = new Quote(quoteForm.getText(), quoteForm.getSource(), a.getId());
            System.out.println("Saving Quote");
            quoteService.save(quote);
            return;
        }
    }
    
    public Quote randomFallback() {
        return new Quote("error", "error", -1L);
    }
    public Quote[] listFallback(String authorName) {
    	Quote[] qs = new Quote[1];
    	qs[1] = new Quote("error", "error", -1L);
    	return qs;
    }
}
