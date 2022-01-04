package com.example.aptedgeservice.controller;

import com.example.aptedgeservice.model.Bestelling;
import com.example.aptedgeservice.model.CompleteBestelling;
import com.example.aptedgeservice.model.Gerecht;
import com.example.aptedgeservice.model.Personeel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class CompleteBestellingController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${personeelservice.baseurl}")
    private String personeelServiceBaseUrl;

    @Value("${gerechtservice.baseurl}")
    private String gerechtServiceBaseUrl;

    @Value("${bestellingservice.baseurl}")
    private String bestellingServiceBaseUrl;

    @GetMapping("/bestellingen/{bestelNummer}")
    public CompleteBestelling getBestellingByBestelNummer(@PathVariable String bestelNummer){
        Bestelling bestelling = restTemplate.getForObject("https://" + bestellingServiceBaseUrl + "/bestellingen/bestelnummer/" + bestelNummer, Bestelling.class);
        if (bestelling != null){
            return vulBestelling(bestelling);
        }
        return null;
    }

    @GetMapping("/bestellingen")
    public List<CompleteBestelling> getBestellingen(){
        ResponseEntity<List<Bestelling>> responseEntityBestellingen = restTemplate.exchange("https://" + bestellingServiceBaseUrl + "/bestellingen", HttpMethod.GET, null, new ParameterizedTypeReference<List<Bestelling>>() {
        });
        List<Bestelling> bestellingen = responseEntityBestellingen.getBody();
        List<CompleteBestelling> completeBestellingen = new ArrayList<>();
        assert bestellingen != null;
        for (Bestelling bestelling: bestellingen){
            if (bestelling != null){
                completeBestellingen.add(vulBestelling(bestelling));
            }
        }
        return completeBestellingen;
    }

    @PostMapping("/bestellingen")
    public CompleteBestelling newBestelling(@RequestBody Bestelling bestelling){
        Bestelling postBestelling = restTemplate.postForObject("https://" + bestellingServiceBaseUrl + "/bestellingen", bestelling, Bestelling.class);
        assert postBestelling != null;
        return vulBestelling(postBestelling);
    }

    @PutMapping("/bestellingen")
    public CompleteBestelling editBestelling(@RequestBody Bestelling bestelling){
        ResponseEntity<Bestelling> responseEntityBestelling = restTemplate.exchange("https://" + bestellingServiceBaseUrl + "/bestellingen", HttpMethod.PUT, new HttpEntity<>(bestelling), Bestelling.class);
        return vulBestelling(responseEntityBestelling.getBody());
    }

    @DeleteMapping("/bestellingen/bestelnummer/{bestelNummer}")
    public ResponseEntity<String> deleteBestelling(@PathVariable String bestelNummer){
        restTemplate.delete("https://" + bestellingServiceBaseUrl + "/bestellingen/bestelnummer/" + bestelNummer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/personeel")
    public List<Personeel> getPersoneel(){
        ResponseEntity<List<Personeel>> responseEntityPersoneel = restTemplate.exchange("https://" + personeelServiceBaseUrl + "/personeel", HttpMethod.GET, null, new ParameterizedTypeReference<List<Personeel>>() {
        });
        return responseEntityPersoneel.getBody();
    }

    @GetMapping("/personeel/{personeelsNummer}")
    public Personeel getPersoneelByPersoneelsNummer(@PathVariable String personeelsNummer){
        return restTemplate.getForObject("https://" + personeelServiceBaseUrl + "/personeel/" + personeelsNummer, Personeel.class);
    }

    @GetMapping("/personeel/functie/{functie}")
    public List<Personeel> getPersoneelByFunctie(@PathVariable String functie){
        ResponseEntity<List<Personeel>> responseEntityPersoneel = restTemplate.exchange("https://" + personeelServiceBaseUrl + "/personeel/functie/" + functie, HttpMethod.GET, null, new ParameterizedTypeReference<List<Personeel>>() {
        });
        return responseEntityPersoneel.getBody();
    }

    @GetMapping("/gerechten")
    public List<Gerecht> getGerechten(){
        ResponseEntity<List<Gerecht>> responseEntityGerechten = restTemplate.exchange("https://" + gerechtServiceBaseUrl + "/gerechten", HttpMethod.GET, null, new ParameterizedTypeReference<List<Gerecht>>() {
        });
        return responseEntityGerechten.getBody();
    }

    @GetMapping("/gerechten/{gerechtNummer}")
    public Gerecht getGerechtByGerechtNummer(@PathVariable String gerechtNummer){
        return restTemplate.getForObject("https://" + gerechtServiceBaseUrl + "/gerechten/" + gerechtNummer, Gerecht.class);
    }

    private CompleteBestelling vulBestelling(Bestelling bestelling) {
        Personeel personeel = restTemplate.getForObject("https://" + personeelServiceBaseUrl + "/personeel/" + bestelling.getPersoneelsNummer(), Personeel.class);

        ArrayList<Gerecht> gerechten = new ArrayList<>();
        for (String gerechtNummer: bestelling.getGerechten()) {
            Gerecht gerecht = restTemplate.getForObject("https://" + gerechtServiceBaseUrl + "/gerechten/" + gerechtNummer, Gerecht.class);
            gerechten.add(gerecht);
        }

        return new CompleteBestelling(bestelling.getBestelNummer(), personeel, gerechten);
    }
}
