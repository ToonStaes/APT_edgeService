package com.example.aptedgeservice;

import com.example.aptedgeservice.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CompleteBestellingControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Value("${personeelservice.baseurl}")
    private String personeelServiceBaseUrl;

    @Value("${gerechtservice.baseurl}")
    private String gerechtServiceBaseUrl;

    @Value("${bestellingservice.baseurl}")
    private String bestellingServiceBaseUrl;

    Personeel arne = new Personeel("Arne", "Hus", Functie.Keuken, "K20220103AH");
    Personeel toon = new Personeel("Toon", "Staes", Functie.Keuken, "K20220103TS");
    Personeel niels = new Personeel("Niels", "Verheyen", Functie.Zaal, "Z20220103NV");

    private List<Personeel> allPersoneel = Arrays.asList(arne, toon, niels);
    private List<Personeel> keukenPersoneel = Arrays.asList(arne, toon);

    Gerecht margherita = new Gerecht("Pizza Margherita", 9.50, "20220103PM");
    Gerecht hawai = new Gerecht("Pizza Hawaï", 99.99, "20200103PH");
    Gerecht salami = new Gerecht("Pizza Salami", 11.00, "20200103PS");

    private List<Gerecht> allGerechten = Arrays.asList(margherita, hawai, salami);

    Bestelling bestellingArne = new Bestelling("1", "K20220103AH", new ArrayList<String>(Arrays.asList("20220103PM", "20200103PH")));
    Bestelling bestellingToon = new Bestelling("2", "K20220103TS", new ArrayList<String>(Arrays.asList("20220103PM", "20200103PS")));

    private List<Bestelling> allBestellingen = Arrays.asList(bestellingArne, bestellingToon);

    CompleteBestelling completeBestellingArne = new CompleteBestelling("1", arne, new ArrayList<Gerecht>(Arrays.asList(margherita, hawai)));
    CompleteBestelling completeBestellingToon = new CompleteBestelling("2", toon, new ArrayList<Gerecht>(Arrays.asList(margherita, salami)));

    private List<CompleteBestelling> allCompleteBestellingen = Arrays.asList(completeBestellingArne, completeBestellingToon);

    @BeforeEach
    public void initializeMockserver(){
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void whenGetBestellingByBestelnummer_thenReturnCompleteBestelling() throws Exception{
        // GET bestelling with bestelnummer 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + bestellingServiceBaseUrl + "/bestellingen/bestelnummer/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(bestellingArne))
                );

        // GET personeel with personeelsnummer K20220103AH
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/K20220103AH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(arne))
                );

        // GET gerecht with gerechtnummer 20220103PM
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20220103PM")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(margherita))
                );

        // GET gerecht with gerechtnummer 20200103PH
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(hawai))
                );

        mockMvc.perform(get("/bestellingen/{bestelnummer}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestelNummer", is("1")))
                .andExpect(jsonPath("$.personeelslid.voornaam", is("Arne")))
                .andExpect(jsonPath("$.personeelslid.achternaam", is("Hus")))
                .andExpect(jsonPath("$.personeelslid.functie", is("Keuken")))
                .andExpect(jsonPath("$.personeelslid.personeelsnummer", is("K20220103AH")))
                .andExpect(jsonPath("$.gerechten[0].naam", is("Pizza Margherita")))
                .andExpect(jsonPath("$.gerechten[0].prijs", is(9.50)))
                .andExpect(jsonPath("$.gerechten[0].gerechtNummer", is("20220103PM")))
                .andExpect(jsonPath("$.gerechten[1].naam", is("Pizza Hawaï")))
                .andExpect(jsonPath("$.gerechten[1].prijs", is(99.99)))
                .andExpect(jsonPath("$.gerechten[1].gerechtNummer", is("20200103PH")));
    }

    @Test
    public void whenGetBestellingen_thenReturnAllCompleteBestellingen() throws Exception{
        // GET bestellingen
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + bestellingServiceBaseUrl + "/bestellingen")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allBestellingen))
                );

        // GET personeel with personeelsnummer K20220103AH
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/K20220103AH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(arne))
                );

        // GET gerecht with gerechtnummer 20220103PM
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20220103PM")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(margherita))
                );

        // GET gerecht with gerechtnummer 20200103PH
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(hawai))
                );

        // GET personeel with personeelsnummer K20220103TS
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/K20220103TS")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(toon))
                );

        // GET gerecht with gerechtnummer 20220103PM
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20220103PM")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(margherita))
                );

        // GET gerecht with gerechtnummer 20200103PS
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PS")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(salami))
                );

        mockMvc.perform(get("/bestellingen"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bestelNummer", is("1")))
                .andExpect(jsonPath("$[0].personeelslid.voornaam", is("Arne")))
                .andExpect(jsonPath("$[0].personeelslid.achternaam", is("Hus")))
                .andExpect(jsonPath("$[0].personeelslid.functie", is("Keuken")))
                .andExpect(jsonPath("$[0].personeelslid.personeelsnummer", is("K20220103AH")))
                .andExpect(jsonPath("$[0].gerechten[0].naam", is("Pizza Margherita")))
                .andExpect(jsonPath("$[0].gerechten[0].prijs", is(9.50)))
                .andExpect(jsonPath("$[0].gerechten[0].gerechtNummer", is("20220103PM")))
                .andExpect(jsonPath("$[0].gerechten[1].naam", is("Pizza Hawaï")))
                .andExpect(jsonPath("$[0].gerechten[1].prijs", is(99.99)))
                .andExpect(jsonPath("$[0].gerechten[1].gerechtNummer", is("20200103PH")))
                .andExpect(jsonPath("$[1].bestelNummer", is("2")))
                .andExpect(jsonPath("$[1].personeelslid.voornaam", is("Toon")))
                .andExpect(jsonPath("$[1].personeelslid.achternaam", is("Staes")))
                .andExpect(jsonPath("$[1].personeelslid.functie", is("Keuken")))
                .andExpect(jsonPath("$[1].personeelslid.personeelsnummer", is("K20220103TS")))
                .andExpect(jsonPath("$[1].gerechten[0].naam", is("Pizza Margherita")))
                .andExpect(jsonPath("$[1].gerechten[0].prijs", is(9.50)))
                .andExpect(jsonPath("$[1].gerechten[0].gerechtNummer", is("20220103PM")))
                .andExpect(jsonPath("$[1].gerechten[1].naam", is("Pizza Salami")))
                .andExpect(jsonPath("$[1].gerechten[1].prijs", is(11.00)))
                .andExpect(jsonPath("$[1].gerechten[1].gerechtNummer", is("20200103PS")));
    }

    @Test
    public void whenAddBestelling_thenReturnCompleteBestelling() throws Exception{
        Bestelling nieuweBestelling = new Bestelling("3", "K20220103AH", new ArrayList<String>(Arrays.asList("20200103PH", "20200103PS")));

        // POST nieuwe bestelling
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + bestellingServiceBaseUrl + "/bestellingen")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(nieuweBestelling))
                );

        // GET personeel with personeelsnummer K20220103AH
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/K20220103AH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(arne))
                );

        // GET gerecht with gerechtnummer 20200103PH
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(hawai))
                );

        // GET gerecht with gerechtnummer 20200103PS
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PS")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(salami))
                );

        mockMvc.perform(post("/bestellingen")
                .content(mapper.writeValueAsString(nieuweBestelling))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestelNummer", is("3")))
                .andExpect(jsonPath("$.personeelslid.voornaam", is("Arne")))
                .andExpect(jsonPath("$.personeelslid.achternaam", is("Hus")))
                .andExpect(jsonPath("$.personeelslid.functie", is("Keuken")))
                .andExpect(jsonPath("$.personeelslid.personeelsnummer", is("K20220103AH")))
                .andExpect(jsonPath("$.gerechten[0].naam", is("Pizza Hawaï")))
                .andExpect(jsonPath("$.gerechten[0].prijs", is(99.99)))
                .andExpect(jsonPath("$.gerechten[0].gerechtNummer", is("20200103PH")))
                .andExpect(jsonPath("$.gerechten[1].naam", is("Pizza Salami")))
                .andExpect(jsonPath("$.gerechten[1].prijs", is(11.00)))
                .andExpect(jsonPath("$.gerechten[1].gerechtNummer", is("20200103PS")));
    }

    @Test
    public void whenUpdateBestelling_thenReturnCompleteBestelling() throws Exception{
        Bestelling updateBestelling = new Bestelling("1", "K20220103AH", new ArrayList<String>(Arrays.asList("20200103PH", "20200103PS")));

        // PUT update bestelling with new pizza's
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + bestellingServiceBaseUrl + "/bestellingen")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(updateBestelling))
                );

        // GET personeel with personeelsnummer K20220103AH
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/K20220103AH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(arne))
                );

        // GET gerecht with gerechtnummer 20200103PH
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(hawai))
                );

        // GET gerecht with gerechtnummer 20200103PS
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20200103PS")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(salami))
                );

        mockMvc.perform(put("/bestellingen")
                .content(mapper.writeValueAsString(updateBestelling))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestelNummer", is("1")))
                .andExpect(jsonPath("$.personeelslid.voornaam", is("Arne")))
                .andExpect(jsonPath("$.personeelslid.achternaam", is("Hus")))
                .andExpect(jsonPath("$.personeelslid.functie", is("Keuken")))
                .andExpect(jsonPath("$.personeelslid.personeelsnummer", is("K20220103AH")))
                .andExpect(jsonPath("$.gerechten[0].naam", is("Pizza Hawaï")))
                .andExpect(jsonPath("$.gerechten[0].prijs", is(99.99)))
                .andExpect(jsonPath("$.gerechten[0].gerechtNummer", is("20200103PH")))
                .andExpect(jsonPath("$.gerechten[1].naam", is("Pizza Salami")))
                .andExpect(jsonPath("$.gerechten[1].prijs", is(11.00)))
                .andExpect(jsonPath("$.gerechten[1].gerechtNummer", is("20200103PS")));
    }

    @Test
    public void whenDeleteBestelling_thenReturnStatusOk() throws Exception{
        // DELETE bestelling
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + bestellingServiceBaseUrl + "/bestellingen/bestelnummer/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockMvc.perform(delete("/bestellingen/bestelnummer/{bestelnummer}", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetPersoneel_thenReturnAllPersoneel() throws Exception{
        // GET personeel
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allPersoneel))
                );

        mockMvc.perform(get("/personeel"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].voornaam", is("Arne")))
                .andExpect(jsonPath("$[0].achternaam", is("Hus")))
                .andExpect(jsonPath("$[0].functie", is("Keuken")))
                .andExpect(jsonPath("$[0].personeelsnummer", is("K20220103AH")))
                .andExpect(jsonPath("$[1].voornaam", is("Toon")))
                .andExpect(jsonPath("$[1].achternaam", is("Staes")))
                .andExpect(jsonPath("$[1].functie", is("Keuken")))
                .andExpect(jsonPath("$[1].personeelsnummer", is("K20220103TS")))
                .andExpect(jsonPath("$[2].voornaam", is("Niels")))
                .andExpect(jsonPath("$[2].achternaam", is("Verheyen")))
                .andExpect(jsonPath("$[2].functie", is("Zaal")))
                .andExpect(jsonPath("$[2].personeelsnummer", is("Z20220103NV")));
    }

    @Test
    public void whenGetPersoneelByPersoneelsNummer_thenReturnPersoneel() throws Exception{
        // GET personeel with personeelsnummer
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/K20220103AH")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(arne))
                );

        mockMvc.perform(get("/personeel/{personeelsnummer}", "K20220103AH"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voornaam", is("Arne")))
                .andExpect(jsonPath("$.achternaam", is("Hus")))
                .andExpect(jsonPath("$.functie", is("Keuken")))
                .andExpect(jsonPath("$.personeelsnummer", is("K20220103AH")));
    }

    @Test
    public void whenGetPersoneelByFunctie_thenReturnPersoneel() throws Exception{
        // GET personeel by functie
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + personeelServiceBaseUrl + "/personeel/functie/keuken")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(keukenPersoneel))
                );

        mockMvc.perform(get("/personeel/functie/{functie}", "keuken"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].voornaam", is("Arne")))
                .andExpect(jsonPath("$[0].achternaam", is("Hus")))
                .andExpect(jsonPath("$[0].functie", is("Keuken")))
                .andExpect(jsonPath("$[0].personeelsnummer", is("K20220103AH")))
                .andExpect(jsonPath("$[1].voornaam", is("Toon")))
                .andExpect(jsonPath("$[1].achternaam", is("Staes")))
                .andExpect(jsonPath("$[1].functie", is("Keuken")))
                .andExpect(jsonPath("$[1].personeelsnummer", is("K20220103TS")));
    }

    @Test
    public void whenGetGerechten_thenReturnAllGerechten() throws Exception{
        // GET gerechten
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allGerechten))
                );

        mockMvc.perform(get("/gerechten"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].naam", is("Pizza Margherita")))
                .andExpect(jsonPath("$[0].prijs", is(9.50)))
                .andExpect(jsonPath("$[0].gerechtNummer", is("20220103PM")))
                .andExpect(jsonPath("$[1].naam", is("Pizza Hawaï")))
                .andExpect(jsonPath("$[1].prijs", is(99.99)))
                .andExpect(jsonPath("$[1].gerechtNummer", is("20200103PH")))
                .andExpect(jsonPath("$[2].naam", is("Pizza Salami")))
                .andExpect(jsonPath("$[2].prijs", is(11.00)))
                .andExpect(jsonPath("$[2].gerechtNummer", is("20200103PS")));
    }

    @Test
    public void whenGetGerechtByGerechtnummer_thenReturnGerecht() throws Exception{
        // GET gerecht by gerechtNummer
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://" + gerechtServiceBaseUrl + "/gerechten/20220103PM")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(margherita))
                );

        mockMvc.perform(get("/gerechten/{gerechtNummer}", "20220103PM"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.naam", is("Pizza Margherita")))
                .andExpect(jsonPath("$.prijs", is(9.50)))
                .andExpect(jsonPath("$.gerechtNummer", is("20220103PM")));
    }
}
