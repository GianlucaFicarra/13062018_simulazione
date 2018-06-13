package it.polito.tdp.flightdelays;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.flightdelays.db.Tratta;
import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlightDelaysController {

	private Model model;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtResult;

    @FXML
    private ComboBox<Airline> cmbBoxLineaAerea;

    @FXML
    private Button caricaVoliBtn;

    @FXML
    private TextField numeroPasseggeriTxtInput;

    @FXML
    private TextField numeroVoliTxtInput;

    @FXML
    void doCaricaVoli(ActionEvent event) {
    	
    		System.out.println("Carica voli!");
    		
    		//salvo l'anno passato dall'utente
    		Airline linea = cmbBoxLineaAerea.getValue();

    		//verifico se la stringa anno non sia vuota
    		if (linea == null) {
    			txtResult.appendText("Scegliere una linea\n");
    			return;
    		}
    		
    		//creo un grafo per calcolare i confini
    		try {
    			model.createGraph(linea);
    			
    		} catch (RuntimeException e) {
    			e.printStackTrace();
    			txtResult.appendText("Errore\n");
    			return;
    		}
    		
    		List<Tratta> list = new ArrayList<>();
    		list= model.getTrattePeggiori();
    		
    		for(Tratta t: list) {
    			this.txtResult.appendText(String.format("%s", t.toString()));
    		}
    		
    		
    }

    @FXML
    void doSimula(ActionEvent event) {
    		System.out.println("Simula!");
    }

    @FXML
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxLineaAerea != null : "fx:id=\"cmbBoxLineaAerea\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert caricaVoliBtn != null : "fx:id=\"caricaVoliBtn\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroPasseggeriTxtInput != null : "fx:id=\"numeroPasseggeriTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert numeroVoliTxtInput != null : "fx:id=\"numeroVoliTxtInput\" was not injected: check your FXML file 'FlightDelays.fxml'.";

    }
    
	public void setModel(Model model) {
	this.model = model;
			
			//setto i valori dell tendina
	       cmbBoxLineaAerea.getItems().addAll(model.getLinee());	
	}
}
