package casco.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckComboBox;

import casco.gui.FileHandler;
import casco.music.Sintetizzatore;
import casco.music.Spartito;
import casco.core.GcgBoard;
import casco.core.GofBoard;
import casco.music.Strumento;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller implements Initializable {

	private final int    DEFAULT_SIZE = 15;			//dimensione delle griglie degli automi cellulari
	private final double DEFAULT_PROB = 0.3;		//probabilità che una cella del Game of Life sia inizialmente viva
	private final int DEFAULT_POSSIBLE_STATE = 9;	//numero di possibili stati di una cella del Griffeath's Crystalline Growths
	private final int MIN_ITERATION = 2;			//numero di iterazione eseguite inizialmente senza estrarre note

	@FXML
	private FlowPane baseGof;
	@FXML
	private FlowPane baseGcg;
	@FXML
	private FlowPane baseGof1;
	@FXML
	private FlowPane baseGcg1;
	@FXML
	private FlowPane baseGof2;
	@FXML
	private FlowPane baseGcg2;
	@FXML
	private FlowPane baseGof3;
	@FXML
	private FlowPane baseGcg3;
	@FXML
	private Label strumentoLabel;
	@FXML
	private Label strumento1Label;
	@FXML
	private Label strumento2Label;
	@FXML
	private Label strumento3Label;
	@FXML
	private Label timeLabel, iterLabel;
	@FXML
	private Button runButton, stopButton, playButton, stopMusicButton, pauseMusicButton, settingsButton, resetButton, reinitializeButton;
	@FXML
	private HBox rootBox;

	private GofBoard[] gofBoards;
	private GcgBoard[] gcgBoards;

	private GofBoard[] gofBoardsInitial;
	private GcgBoard[] gcgBoardsInitial;
	int num = 16;
	int bpm = 60;
	private int state = 0;
	
	private JavaFXDisplayDriver display;

	private Timeline loop = null;
	private Timeline loopMusic = null;
	private int time = 0;
	private int iterCount = 0;

	private int cellSizePx = 5;

	private ArrayList<Strumento> orchestra = new ArrayList <Strumento>();
	private Spartito[] spartiti;
	HashMap<String, int[][]> mappaScale;
	HashMap<String, int[]> mappaRepeat;
	private Sintetizzatore sint;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			mappaScale = new HashMap<String, int[][]>();
			mappaRepeat = new HashMap<String, int[]>();
			
			defineOrchestra();
			sint = new Sintetizzatore(orchestra);
		} catch (Exception e) {
			e.printStackTrace();
		}
		start();
	}
	
	private void start(){
		state = 0;
		iterCount = 0;
		time = 0;
		bpm = 60;
		
		gofBoards = new GofBoard[num];
		gcgBoards = new GcgBoard[num];
		spartiti = new Spartito[num];
		
		strumentoLabel.setText(orchestra.get(0).getName());
		if(orchestra.size() > 1) strumento1Label.setText(orchestra.get(1).getName()); else strumento1Label.setText("[Null]");
		if(orchestra.size() > 2) strumento2Label.setText(orchestra.get(2).getName()); else strumento1Label.setText("[Null]");
		if(orchestra.size() > 3) strumento3Label.setText(orchestra.get(3).getName()); else strumento1Label.setText("[Null]");

		for(int i = 0; i < num; i++){		//creazione griglie automi cellulari con valori casuali
			createBoardGof(i, DEFAULT_SIZE, DEFAULT_PROB);
			createBoardGcg(i, DEFAULT_SIZE, DEFAULT_POSSIBLE_STATE);
			spartiti[i] = new Spartito(orchestra.get(i));	//ad ogni spartito viene assegnato uno strumento
			if(spartiti[i].getStrumento().isPercussione())
				spartiti[i].getStrumento().setOrchestraIndex(9);	//le percussioni vengono tutte suonate sul canale 9 del 
		}															//sintetizzatore per impostazioni del MIDI
		gofBoardsInitial = new GofBoard[num];
		gcgBoardsInitial = new GcgBoard[num];
		for(int i = 0; i < num; i++){		//Vengono salvate le griglie iniziali per poterle recuperare un caso di reset
			gofBoardsInitial[i] = new GofBoard(gofBoards[i]);
			gcgBoardsInitial[i] = new GcgBoard(gcgBoards[i]);
		}
		createDisplay();	//creazione interfaccia
		timeLabel.setText(new Integer(time).toString());
		iterLabel.setText(new Integer(iterCount).toString());
		state = 0;
		changeState(state);
	}
	
	public void defineOrchestra(){
		//Scale di defualt:
		
		int[][] eMinorPentatonic = {{40, 43, 45, 47, 50, 52, 55, 57, 59, 62, 64, 67, 69, 71, 74, 76}};//, 79, 81, 83, 86, 88, 91};
		mappaScale.put("eMinorPentatonic", eMinorPentatonic);
		//GO WELL TOGETHER
		int[][] cMajor = {{48, 50, 52, 53, 55, 57, 59, 60}};
		mappaScale.put("cMajor", cMajor);
		int[][] armonyC = { {36, 40, 43}, {38, 41, 45}, {40, 43, 47}, {41, 45, 48}, {43, 47, 50}, {45, 48, 52}, {47, 50, 53}};
		mappaScale.put("armonyC", armonyC);
		int[][] aMinorPentatonic = {{45, 48, 50, 52, 55, 57, 60, 62, 67, 69}};
		mappaScale.put("aMinorPentatonic", aMinorPentatonic);

		//GO WELL TOGETHER
		int[][] aMinScale = {{45, 47, 48, 50, 52, 53, 56}};
		mappaScale.put("aMinScale", aMinScale);
		int[][] aMinArmony = {{33, 36, 40}, {35, 38, 41}, {36, 40, 44}, {38, 41, 45}, {40, 44, 47}, {41, 45, 48}, {44, 47, 50}};
		mappaScale.put("aMinArmony", aMinArmony);

		int[][] scalaViolino = {{50, 52, 55, 57, 59, 62, 64, 67, 69, 71, 74}};
		mappaScale.put("scalaViolino", scalaViolino);
		int[][] drums = {{60}};
		mappaScale.put("drums", drums);
		int[] repeatNull = null;
		mappaRepeat.put("[Null]", repeatNull);
		int[] repeat1 = {1};
		mappaRepeat.put("1", repeat1);
		int[] repeat12 = {1, 2};
		mappaRepeat.put("1, 2", repeat12);
		int[] repeat123 = {1, 2, 3};
		mappaRepeat.put("1, 2, 3", repeat123);
		
		//strumenti di defualt:
		
		Strumento s;   
		s = new Strumento("Piano 1", 1, 1, 0, true);
		s.setLunghezzaGcg(0);
		s.setQuartina(64);
		s.setOttava(12);
		s.setInizio(0);
		s.setRepeat(mappaRepeat.get("1, 2"), "1, 2");
		s.setScala(mappaScale.get("armonyC"), "armonyC");
		s.setForzaOn(50);
		orchestra.add(s);
		
		s = new Strumento("Pedal Hi-Hat", 44, 1, 32, true);
		s.setPercussione(true);
		int[][] scalaPercussione = {{44}};
		s.setScala(scalaPercussione, "scala percussione");
		s.setLunghezzaGcg(0);
		s.setForzaOn(20);
		s.setRepeat(mappaRepeat.get("1"), "1");
		orchestra.add(s);

		s = new Strumento("Piano 1", 1, 1, 0, false);
		s.setScala(mappaScale.get("cMajor"), "cMajor");
		s.setForzaOn(82);
		s.setInizio(128);
		s.setContinuaPer(0);
		s.setPausa(0);
		s.setRipetiPer(100);
		s.setOttava(12);
		orchestra.add(s);

		s = new Strumento("Voice Oohs", 53, 1, 0, true);
		s.setScala(mappaScale.get("cMajor"), "cMajor");
		s.setInizio(512);
		s.setContinuaPer(0);
		s.setOttava(12);
		s.setLunghezzaGcg(0);
		s.setRepeat(mappaRepeat.get("1, 2"), "1, 2");
		s.setPausa(0);
		s.setQuartina(128);
		s.setRipetiPer(100);
		s.setForzaOn(30);
		orchestra.add(s);
		
		s = new Strumento("Pan Flute", 75, 1, 0, true);
		s.setScala(mappaScale.get("cMajor"), "cMajor");
		s.setInizio(512);
		s.setContinuaPer(192);
		s.setOttava(24);
		s.setLunghezzaGcg(0);
		s.setPausa(320);
		s.setQuartina(64);
		s.setRipetiPer(100);
		s.setForzaOn(35);
		orchestra.add(s);
		
		for(int i = 0; i < 16; i++){	//riempio i canali restanti con strumenti nulli che non suonano
			s = new Strumento("[Null]", 0, 0, 0);
			s.setRipetiPer(0);
			orchestra.add(s);
		}
		
		for(int i = 0 ; i < orchestra.size(); i++){
			orchestra.get(i).setOrchestraIndex(i);
		}
	}
	
	@FXML
	private void onReinitialize(Event evt) {		//tasto Reinitialize
		start();									//reinizializza gli automi cellulari con nuovi valori random
	}
	
	@FXML
	private void onReset(Event evt) {		//tasto Reset
		state = 0;							//gli automi cellulari vengono reimpostati come nelle impostazioni iniziali
		changeState(state);					
		iterCount = 0;
		time = 0;
		iterLabel.setText(new Integer(iterCount).toString());
		
		for(int i = 0; i < num; i++){
			gofBoards[i] = new GofBoard(gofBoardsInitial[i]);
			gcgBoards[i] = new GcgBoard(gcgBoardsInitial[i]);
			spartiti[i] = new Spartito(orchestra.get(i));
		}
		display.displayBoardGof(gofBoards[0]);
		if(gofBoards.length > 1) display.displayBoardGof1(gofBoards[1]);
		if(gofBoards.length > 2) display.displayBoardGof2(gofBoards[2]);
		if(gofBoards.length > 3) display.displayBoardGof3(gofBoards[3]);
		display.displayBoardGcg(gcgBoards[0]);
		if(gcgBoards.length > 1) display.displayBoardGcg1(gcgBoards[1]);
		if(gcgBoards.length > 2) display.displayBoardGcg2(gcgBoards[2]);
		if(gcgBoards.length > 3) display.displayBoardGcg3(gcgBoards[3]);
	}
	
	@FXML
	private void onRun(Event evt) {		//tatso Run: update CA ed estrazione
		if(state == 0 || state == 2)
			state = 1;
		if(state == 4)
			state = 5;
		changeState(state);

		loop = new Timeline(new KeyFrame(Duration.millis(300), e -> {		//loop che aggiorna tutti i CA
			for(int i = 0; i < num; i++){									//e ad ogni iterazione estrae le note da essi
				gofBoards[i].update();										//inserendole nei vari spartiti
				gcgBoards[i].update();
				spartiti[i].estrazione(gofBoards[i], gcgBoards[i]);
			}
			iterCount++;
			iterLabel.setText(new Integer(iterCount).toString());
			display.displayBoardGof(gofBoards[0]);
			if(gofBoards.length > 1) display.displayBoardGof1(gofBoards[1]);
			if(gofBoards.length > 2) display.displayBoardGof2(gofBoards[2]);
			if(gofBoards.length > 3) display.displayBoardGof3(gofBoards[3]);
			display.displayBoardGcg(gcgBoards[0]);
			if(gcgBoards.length > 1) display.displayBoardGcg1(gcgBoards[1]);
			if(gcgBoards.length > 2) display.displayBoardGcg2(gcgBoards[2]);
			if(gcgBoards.length > 3) display.displayBoardGcg3(gcgBoards[3]);
		}));
		
		if(iterCount == 0){								//le prime MIN_ITERATION vengono eseguite senza estrarre note dagli automi
			for(int i = 0; i < MIN_ITERATION; i++){		//perchè non avrebbe senso visto che hanno valori che derivano quasi completamente
				for(int j=0; j<num; j++){				//dal caso, dopo poche iterazioni invece i valori derivano delle regole dei CA
					gofBoards[j].update();
					gcgBoards[j].update();
				}
				iterCount++;
				iterLabel.setText(new Integer(iterCount).toString());
			}
		}
		
		loop.setCycleCount(100);
		loop.play();
	}

	@FXML
	private void onStop(Event evt) throws InterruptedException {	//tasto Stop: interrompe il loop generato dal tasto Run
		if(state == 1)												//e esegue la translate degli spartiti per ottenere una
			state = 2;												//struttura dati che contiene ON e OFF di tutte le note 
		if(state == 5)												//in modo ordinato
			state = 4;
		changeState(state);
		
		loop.stop();

		for(int i = 0; i < num ; i++){
			spartiti[i].translate();
		}
		time = 0;
		timeLabel.setText(new Integer(time).toString());
	}
	
	@FXML
	private void onPlay(Event evt) throws InterruptedException {	//tasto Play(musica) genera un loop che legge le 
		state = 3;													//strutture dati derivate dalla translate e suona 
		changeState(state);											//le note scandendo il tempo nel modo corretto
		int quartina = spartiti[0].getStrumento().getQuartina();
		double spacing = 60000/bpm/quartina;

		loopMusic = new Timeline(new KeyFrame(Duration.millis(spacing), e -> {
			try {
				sint.playFlowOnce(spartiti, bpm, time);
				time++;
				timeLabel.setText(new Integer(time).toString());
			} catch (Exception e1) {

				e1.printStackTrace();
			}
		}));

		loopMusic.setCycleCount(spartiti[0].getFlow().size());
		loopMusic.play();
	}
	
	@FXML
	private void onPauseMusic(Event evt) {		//tasto Pause(musica) interrompe il loop generato dal tasto Play(musica)
		state = 4;
		changeState(state);
		loopMusic.stop();
		sint.stopMusic();
	}
	
	@FXML
	private void onStopMusic(Event evt) {	//tasto Stop(musica) interrompe il loop generato dal tasto Play(musica)
		state = 4;							//e imposta time = 0
		changeState(state);
		loopMusic.stop();
		sint.stopMusic();
		time = 0;
		timeLabel.setText(new Integer(time).toString());
	}

	@FXML
	private void onSettings(Event evt) throws Exception {	//tasto Settings costrisce l'interfaccia per modificare i parametri degli strumenti
		double width = 50.0;								
		
		Strumento[] strumentiTemp = new Strumento[num];		//viene creato un Array di strumenti temporaneo in modo da poter modificare i parametri
		for(int i = 0; i < num; i++){						//liberamente e rendere le modifiche attive solo dopo aver premuto il tasto OK
			strumentiTemp[i] = new Strumento(spartiti[i].getStrumento());
		}
		
		//descrizione iniziale
		Text text1 = new Text("Settaggio strumenti\n");
		text1.setFont(Font.font(30));
		Text text2 = new Text(
				"\nScegli i parametri relativi agli strumenti e premi OK per confermare le modifiche.");
		TextFlow tf = new TextFlow(text1,text2);
		tf.setPadding(new Insets(10, 10, 10, 10));
		tf.setTextAlignment(TextAlignment.JUSTIFY);
		
		//creazione finestra
		final Stage dialog = new Stage();
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(new Stage());
		dialog.setTitle("CAsCO - Settings");
		Image image = new Image(getClass().getResourceAsStream("CAsCOicon.png"));
        dialog.getIcons().add(image);
		VBox dialogVbox = new VBox(20);
		dialogVbox.getChildren().add(tf);
		
		//barra superiore
		HBox bpmHBox = new HBox();
		Label bpmLabel = new Label("BPM:");
		TextField textBpm = new TextField(new Integer(bpm).toString());		//bmp
		textBpm.setId("textBpm");
		textBpm.setMaxWidth(width);
		bpmHBox.getChildren().add(bpmLabel);
		bpmHBox.getChildren().add(textBpm);
		Button salvaSet = new Button("Salva parametri su file");	//button per salvare file
		salvaSet.setTranslateX(20);
		Button apriSet = new Button("Apri parametri da file");		//button per aprire file
		apriSet.setTranslateX(20);
		bpmHBox.getChildren().add(salvaSet);
		bpmHBox.getChildren().add(apriSet);
		bpmHBox.setSpacing(10);
		Button submit = new Button("OK");							//button OK
		submit.setTranslateX(50);
		bpmHBox.getChildren().add(submit);
		Label debug = new Label("debug OK");						//label non visibile per debug
		debug.setTranslateX(50);
		//bpmHBox.getChildren().add(debug);
		
		dialogVbox.getChildren().add(bpmHBox);
		
		//liste per comboBox
		ScrollPane sp = new ScrollPane();
		ObservableList<String> strumenti = 
				FXCollections.observableArrayList(
						sint.listOfInstruments().keySet()
						);
		ObservableList<String> percussioni = 
				FXCollections.observableArrayList(
						sint.listOfPercussions().keySet()
						);
		ObservableList<String> veroFalso = 
				FXCollections.observableArrayList(
							"Sì",
							"No"
						);
		ObservableList<String> listaScale = 
				FXCollections.observableArrayList(
							mappaScale.keySet()
						);
		ObservableList<String> listaRepeat = 
				FXCollections.observableArrayList(
							mappaRepeat.keySet()
						);
		
		Strumento s;
		for(int i = 0; i < num; i++){		//loop che genera i vari parametri per ogni strumento
			if(i == 9)
				continue;	//il canale 9 è dedicato alle percussioni.
			s = strumentiTemp[i];
			
			HBox hb = new HBox();
			HBox hb1 = new HBox();
			
			VBox indexVBox = new VBox();					//Seleziona strumento
			Label indexLabel;
			if(i <= 8)
				indexLabel = new Label("Strumento " + (i+1));
			else
				indexLabel = new Label("Strumento " + (i));
			indexLabel.setFont(Font.font(22));
			ComboBox<String> comboStrum = new ComboBox<String>(strumenti);
			comboStrum.setId("comboStrum" + i);
			comboStrum.setValue(s.getName());
			indexVBox.getChildren().add(indexLabel);
			indexVBox.getChildren().add(comboStrum);
															//Seleziona Percussione
			CheckComboBox<String> comboStrumPerc = new CheckComboBox<String>(percussioni);
			comboStrumPerc.setId("comboStrumPerc" + i);
			String[] listPerc = s.getName().split(":");
			for(String str : listPerc){
				comboStrumPerc.getCheckModel().check(str);
			}
			indexVBox.getChildren().add(comboStrumPerc);
			comboStrumPerc.setMaxWidth(127);
			
			
			VBox sincrVBox = new VBox();					//Sincronizzazione
			Label sincrLabel = new Label("Sincroniz.");
			ComboBox<String> comboSincr = new ComboBox<String>(veroFalso);
			comboSincr.setId("comboSincr" + i);
			if(s.isSicronizzazione())
				comboSincr.setValue("Sì");
			else
				comboSincr.setValue("No");
			sincrVBox.getChildren().add(sincrLabel);
			sincrVBox.getChildren().add(comboSincr);
			
			VBox scaleVBox = new VBox();					//Scala
			Label scaleLabel = new Label("Scala");
			ComboBox<String> comboScale = new ComboBox<String>(listaScale);
			comboScale.setId("comboScale" + i);
			comboScale.setValue(s.getNomeScala());
			scaleVBox.getChildren().add(scaleLabel);
			scaleVBox.getChildren().add(comboScale);
			
			VBox ottavaVBox = new VBox();					//Ottava
			Label ottavaLabel = new Label("Ottava");
			TextField textOttava = new TextField(new Integer(s.getOttava()).toString());
			textOttava.setId("textOttava" + i);
			textOttava.setMaxWidth(width);
			ottavaVBox.getChildren().add(ottavaLabel);
			ottavaVBox.getChildren().add(textOttava);
			
			VBox forzaVBox = new VBox();					//Forza
			Label forzaLabel = new Label("Forza");
			TextField textForza = new TextField(new Integer(s.getForzaOn()).toString());
			textForza.setId("textForza" + i);
			textForza.setMaxWidth(width);
			forzaVBox.getChildren().add(forzaLabel);
			forzaVBox.getChildren().add(textForza);
			
			VBox inizioVBox = new VBox();					//Inizio
			Label inizioLabel = new Label("Inizio");
			TextField textInizio = new TextField(new Integer(s.getInizio()).toString());
			textInizio.setId("textInizio" + i);
			textInizio.setMaxWidth(width + 15);
			inizioVBox.getChildren().add(inizioLabel);
			inizioVBox.getChildren().add(textInizio);
			
			VBox continuaVBox = new VBox();					//Continua per
			Label continuaLabel = new Label("Continua per");
			TextField textContinua = new TextField(new Integer(s.getContinuaPer()).toString());
			textContinua.setId("textContinua" + i);
			textContinua.setMaxWidth(width + 15);
			continuaVBox.getChildren().add(continuaLabel);
			continuaVBox.getChildren().add(textContinua);
			
			VBox pausaVBox = new VBox();					//Pausa
			Label pausaLabel = new Label("Pausa");
			TextField textPausa = new TextField(new Integer(s.getPausa()).toString());
			textPausa.setId("textPausa" + i);
			textPausa.setMaxWidth(width + 15);
			pausaVBox.getChildren().add(pausaLabel);
			pausaVBox.getChildren().add(textPausa);
			
			VBox ripetiPerVBox = new VBox();				//Ripeti per
			Label ripetiPerLabel = new Label("Ripeti per");
			TextField textRipetiPer = new TextField(new Integer(s.getRipetiPer()).toString());
			textRipetiPer.setId("textRipetiPer" + i);
			textRipetiPer.setMaxWidth(width);
			ripetiPerVBox.getChildren().add(ripetiPerLabel);
			ripetiPerVBox.getChildren().add(textRipetiPer);
			
			VBox quartinaVBox = new VBox();					//Quartina
			Label quartinaLabel = new Label("Quartina");
			TextField textQuartina = new TextField(new Integer(s.getQuartina()).toString());
			textQuartina.setId("textQuartina" + i);
			textQuartina.setMaxWidth(width);
			quartinaVBox.getChildren().add(quartinaLabel);
			quartinaVBox.getChildren().add(textQuartina);

			
			VBox lungNotaVBox = new VBox();					//Lunghezza nota statica
			Label lungNotaLabel = new Label("Lunghezza\nnota statica");
			TextField textLungNota = new TextField(new Double(s.getLunghezzaNota()).toString());
			textLungNota.setId("textLungNota" + i);
			textLungNota.setMaxWidth(width);
			lungNotaVBox.getChildren().add(lungNotaLabel);
			lungNotaVBox.getChildren().add(textLungNota);
			
			VBox lungGcgVBox = new VBox();					//Lunghezza nota dinamica
			Label lungGcgLabel = new Label("Lunghezza\nnota dinamica");
			TextField textLungGcg = new TextField(new Double(s.getLunghezzaGcg()).toString());
			textLungGcg.setId("textLungGcg" + i);
			textLungGcg.setMaxWidth(width);
			lungGcgVBox.getChildren().add(lungGcgLabel);
			lungGcgVBox.getChildren().add(textLungGcg);
			
			VBox delayVBox = new VBox();					//Delay
			Label delayLabel = new Label("Delay");
			TextField textDelay = new TextField(new Double(s.getDelay()).toString());
			textDelay.setId("textDelay" + i);
			textDelay.setMaxWidth(width);
			delayVBox.getChildren().add(delayLabel);
			delayVBox.getChildren().add(textDelay);
			
			VBox gapQuartinaVBox = new VBox();				//Gap quartina
			Label gapQuartinaLabel = new Label("Gap quartina");
			TextField textGapQuartina = new TextField(new Integer(s.getGapQuartina()).toString());
			textGapQuartina.setId("textGapQuartina" + i);
			textGapQuartina.setMaxWidth(width);
			gapQuartinaVBox.getChildren().add(gapQuartinaLabel);
			gapQuartinaVBox.getChildren().add(textGapQuartina);
			
			VBox repeatVBox = new VBox();					//Repeat
			Label repeatLabel = new Label("Repeat");
			ComboBox<String> comboRepeat = new ComboBox<String>(listaRepeat);
			comboRepeat.setId("comboRepeat" + i);
			comboRepeat.setValue(s.getNomeRepeat());
			repeatVBox.getChildren().add(repeatLabel);
			repeatVBox.getChildren().add(comboRepeat);
			
			VBox percusVBox = new VBox();					//Percussione? Sì o No
			Label percusLabel = new Label("Percussione");	//Se lo strumento è una percussione è gestito in modo diverso
			ComboBox<String> comboPercussione = new ComboBox<String>(veroFalso);
			comboPercussione.setId("comboPercussione" + i);
			if(s.isPercussione())
				comboPercussione.setValue("Sì");
			else
				comboPercussione.setValue("No");
			percusVBox.getChildren().add(percusLabel);
			percusVBox.getChildren().add(comboPercussione);
			comboPercussione.valueProperty().addListener(new ChangeListener<String>() {
		        @Override public void changed(ObservableValue ov, String t, String t1) {
		        	if(t1.equalsIgnoreCase("No")){
						comboStrumPerc.setVisible(false);
						comboStrum.setVisible(true);
						scaleVBox.setVisible(true);
		        		ottavaVBox.setVisible(true);
		        	}else{
		        		comboStrumPerc.setVisible(true);
		        		comboStrum.setVisible(false);
		        		scaleVBox.setVisible(false);
		        		ottavaVBox.setVisible(false);
		        	}
		        }    
		    });
			if(s.isPercussione()){
				comboStrumPerc.setVisible(true);
				comboStrum.setVisible(false);
				scaleVBox.setVisible(false);
        		ottavaVBox.setVisible(false);
        	}else{
        		comboStrumPerc.setVisible(false);
        		comboStrum.setVisible(true);
        		scaleVBox.setVisible(true);
        		ottavaVBox.setVisible(true);
        	}
			
			hb.getChildren().addAll(indexVBox, sincrVBox, percusVBox, scaleVBox, ottavaVBox, forzaVBox, inizioVBox, continuaVBox, pausaVBox, ripetiPerVBox, quartinaVBox);
			hb.setSpacing(10);
			
			hb1.getChildren().addAll(lungNotaVBox, lungGcgVBox, delayVBox, gapQuartinaVBox, repeatVBox);
			hb1.setTranslateX(155);
			hb1.setTranslateY(-27.5);
			hb1.setSpacing(10);
			
			dialogVbox.getChildren().add(hb);
			dialogVbox.getChildren().add(hb1);
		}
		dialogVbox.setTranslateX(10);
		
		submit.setOnAction(new EventHandler<ActionEvent>() {	//Azione per il Button OK
																//vengono letti i parametri presenti nell'interfaccia
			@Override											//e vengono resi effettivi
		    public void handle(ActionEvent e) {
				try {
					String name;
					Integer index;
					int[][] scala;
					int[] repeat;
																//leggi bpm
					TextField textBpm = (TextField) dialogVbox.lookup("#textBpm");
					if(textBpm != null){
						name = textBpm.getText();
						if(name != null && !name.equals("")){
							Integer bmp = new Integer(name);
							if(bmp != null)
								bpm = bmp.intValue();
						}
					}
//					System.out.println("MAPPA TO STRING:");
//					System.out.println(sint.listOfInstruments().toString());
					
					for(int i = 0; i < num; i++){				//loop per leggere tutti gli strumenti
						if(i == 9)
							continue;	//il canale 9 è dedicato alle percussioni.
						
																//leggi strumento
						ComboBox<String> comboStrum = (ComboBox) dialogVbox.lookup("#comboStrum" + i);
						if(comboStrum != null){
							name = comboStrum.getValue().toString();
							index = sint.listOfInstruments().get(name);
							if(index != null){
								System.out.println(i + "  name: " + name + " index: " + index.intValue());
								spartiti[i].getStrumento().setName(name);
								spartiti[i].getStrumento().setStrumentIndex(index.intValue());
								spartiti[i].getStrumento().setOrchestraIndex(i);
								
								orchestra.get(i).setName(name);
								orchestra.get(i).setStrumentIndex(index.intValue());
								if(i == 0)
									strumentoLabel.setText(name);
								if(i == 1)
									strumento1Label.setText(name);
								if(i == 2)
									strumento2Label.setText(name);
								if(i == 3)
									strumento3Label.setText(name);
								
								sint.changeChannel(i, index.intValue());
							}else
								System.out.println(i + "  name: " + name + " index: NULL");
						}
																//leggi Sincronizzazione
						ComboBox<String> comboSincr = (ComboBox) dialogVbox.lookup("#comboSincr" + i);
						if(comboSincr != null){
							name = comboSincr.getValue().toString();
							if(name != null && !name.equals(""))
								if(name.equalsIgnoreCase("No"))
									spartiti[i].getStrumento().setSicronizzazione(false);
								else
									spartiti[i].getStrumento().setSicronizzazione(true);
						}
																//leggi se è Percussione
						ComboBox<String> comboPercussione = (ComboBox) dialogVbox.lookup("#comboPercussione" + i);
						if(comboPercussione != null){
							name = comboPercussione.getValue().toString();
							if(name != null && !name.equals(""))
								if(name.equalsIgnoreCase("No")){
									spartiti[i].getStrumento().setPercussione(false);
									System.out.println("set perc false");
								}
								else{
									spartiti[i].getStrumento().setPercussione(true);
									System.out.println("set perc true");
								}
						}
																//leggi Strumento se è percussione
						if(spartiti[i].getStrumento().isPercussione()){
							CheckComboBox<String> comboStrumPerc = (CheckComboBox) dialogVbox.lookup("#comboStrumPerc" + i);
							if(comboStrumPerc != null){
								ObservableList<String> checkedList = comboStrumPerc.getCheckModel().getCheckedItems();
								name = "";
								int[][] sca = new int[1][checkedList.size()];	//viene costruita una scala con una sola nota
								for(int j = 0; j < checkedList.size(); j++){	//per far suonare nel modo corretto la percussione selezionata
									String s = checkedList.get(j);
									name = name + s + ":";
									index = sint.listOfPercussions().get(s);
									if(index != null){
										sca[0][j] = index.intValue();
									}
								}
								spartiti[i].getStrumento().setName(name);
								spartiti[i].getStrumento().setOrchestraIndex(9);	//canale dedicato alle percussioni
								spartiti[i].getStrumento().setScala(sca, "scala percussione");
								spartiti[i].getStrumento().setOttava(0);
								
								if(i == 0)
									strumentoLabel.setText(name.replace(":", "\n"));
								if(i == 1)
									strumento1Label.setText(name.replace(":", "\n"));
								if(i == 2)
									strumento2Label.setText(name.replace(":", "\n"));
								if(i == 3)
									strumento3Label.setText(name.replace(":", "\n"));
							}
						}
																//se non è percussione
						if(!spartiti[i].getStrumento().isPercussione()){
																//leggi Scala
							ComboBox<String> comboScale = (ComboBox) dialogVbox.lookup("#comboScale" + i);
							if(comboScale != null){
								name = comboScale.getValue().toString();
								scala = mappaScale.get(name);
								if(scala != null)
									spartiti[i].getStrumento().setScala(scala, name);
							}
																//leggi Ottava
							TextField textOttava = (TextField) dialogVbox.lookup("#textOttava" + i);
							if(textOttava != null){
								name = textOttava.getText();
								if(name != null && !name.equals("")){
									Integer ottava = new Integer(name);
									if(ottava != null)
										spartiti[i].getStrumento().setOttava(ottava.intValue());
								}
							}
						}
																//leggi Forza
						TextField textForza = (TextField) dialogVbox.lookup("#textForza" + i);
						if(textForza != null){
							name = textForza.getText();
							if(name != null && !name.equals("")){
								Integer forza = new Integer(name);
								if(forza != null)
									spartiti[i].getStrumento().setForzaOn(forza.intValue());
							}
						}
																//leggi Inizio
						TextField textInizio = (TextField) dialogVbox.lookup("#textInizio" + i);
						if(textInizio != null){
							name = textInizio.getText();
							if(name != null && !name.equals("")){
								Integer inizio = new Integer(name);
								if(inizio != null)
									spartiti[i].getStrumento().setInizio(inizio.intValue());
							}
						}
																//leggi Continua
						TextField textContinua = (TextField) dialogVbox.lookup("#textContinua" + i);
						if(textContinua != null){
							name = textContinua.getText();
							if(name != null && !name.equals("")){
								Integer continua = new Integer(name);
								if(continua != null)
									spartiti[i].getStrumento().setContinuaPer(continua.intValue());
							}
						}
																//leggi Pausa
						TextField textPausa = (TextField) dialogVbox.lookup("#textPausa" + i);
						if(textPausa != null){
							name = textPausa.getText();
							if(name != null && !name.equals("")){
								Integer pausa = new Integer(name);
								if(pausa != null)
									spartiti[i].getStrumento().setPausa(pausa.intValue());
							}
						}
																//leggi Ripeti per
						TextField textRipetiPer = (TextField) dialogVbox.lookup("#textRipetiPer" + i);
						if(textRipetiPer != null){
							name = textRipetiPer.getText();
							if(name != null && !name.equals("")){
								Integer ripetiPer = new Integer(name);
								if(ripetiPer != null)
									spartiti[i].getStrumento().setRipetiPer(ripetiPer.intValue());
							}
						}
																//leggi Quartina
						TextField textQuartina = (TextField) dialogVbox.lookup("#textQuartina" + i);
						if(textQuartina != null){
							name = textQuartina.getText();
							if(name != null && !name.equals("")){
								Integer quartina = new Integer(name);
								if(quartina != null)
									spartiti[i].getStrumento().setQuartina(quartina.intValue());
							}
						}
																//leggi Lunghezza nota statica
						TextField textLungNota = (TextField) dialogVbox.lookup("#textLungNota" + i);
						if(textLungNota != null){
							name = textLungNota.getText();
							if(name != null && !name.equals("")){
								Double lungNota = new Double(name);
								if(lungNota != null)
									spartiti[i].getStrumento().setLunghezzaNota(lungNota.doubleValue());
							}
						}
																//leggi Lunghezza nota dinamica
						TextField textLungGcg = (TextField) dialogVbox.lookup("#textLungGcg" + i);
						if(textLungGcg != null){
							name = textLungGcg.getText();
							if(name != null && !name.equals("")){
								Double lungGcg = new Double(name);
								if(lungGcg != null)
									spartiti[i].getStrumento().setLunghezzaGcg(lungGcg.doubleValue());
							}
						}
																//leggi Delay
						TextField textDelay = (TextField) dialogVbox.lookup("#textDelay" + i);
						if(textDelay != null){
							name = textDelay.getText();
							if(name != null && !name.equals("")){
								Double delay = new Double(name);
								if(delay != null)
									spartiti[i].getStrumento().setDelay(delay.doubleValue());
							}
						}
																//leggi Gap quartina
						TextField textGapQuartina = (TextField) dialogVbox.lookup("#textGapQuartina" + i);
						if(textGapQuartina != null){
							name = textGapQuartina.getText();
							if(name != null && !name.equals("")){
								Integer gapQuartina = new Integer(name);
								if(gapQuartina != null)
									spartiti[i].getStrumento().setGapQuartina(gapQuartina.intValue());
							}
						}
																//leggi Repeat
						ComboBox<String> comboRepeat = (ComboBox) dialogVbox.lookup("#comboRepeat" + i);
						if(comboRepeat != null){
							name = comboRepeat.getValue().toString();
							repeat = mappaRepeat.get(name);
							spartiti[i].getStrumento().setRepeat(repeat, name);
						}
					}
				}catch (Exception e1) {
					e1.printStackTrace();
					debug.setText(e1.getMessage());
				}finally{
					dialog.close();
				}
		     }
		 });
		
		salvaSet.setOnAction(new EventHandler<ActionEvent>() {		//azione per il Button Salva su file

			@Override
		    public void handle(ActionEvent e) {
				try {
					FileHandler.saveSettingsToFile(strumentiTemp);
				}catch (Exception e1) {
					e1.printStackTrace();
					debug.setText(e1.getMessage());
				}
			}
		});
		apriSet.setOnAction(new EventHandler<ActionEvent>() {		//azione per il Button apri da file

			@Override
		    public void handle(ActionEvent e) {
				try {
					FileHandler.openSettingsFromFile(strumentiTemp, mappaScale, mappaRepeat);
					
					for(int i = 0; i < num; i++){		//vengono letti i parametri dal file e inseriti 
														//nell'interfaccia per ogni strumento
						//strumento
						ComboBox<String> comboStrum = (ComboBox) dialogVbox.lookup("#comboStrum" + i);
						comboStrum.setValue(strumentiTemp[i].getName());
						//sincornizzazione					
						ComboBox<String> comboSincr = (ComboBox) dialogVbox.lookup("#comboSincr" + i);
						if(strumentiTemp[i].isSicronizzazione())
							comboSincr.setValue("Sì");
						else
							comboSincr.setValue("No");
						//è percussione
						ComboBox<String> comboPercussione = (ComboBox) dialogVbox.lookup("#comboPercussione" + i);
						if(strumentiTemp[i].isPercussione())
							comboPercussione.setValue("Sì");
						else
							comboPercussione.setValue("No");
						//strumento se è percussione
						if(strumentiTemp[i].isPercussione()){
							CheckComboBox<String> comboStrumPerc = (CheckComboBox) dialogVbox.lookup("#comboStrumPerc" + i);
							String[] listPerc = strumentiTemp[i].getName().split(":");
							for(String str : listPerc){
								comboStrumPerc.getCheckModel().check(str);
							}
						}
						//scala
						ComboBox<String> comboScale = (ComboBox) dialogVbox.lookup("#comboScale" + i);
						comboScale.setValue(strumentiTemp[i].getNomeScala());
						//ottava
						TextField textOttava = (TextField) dialogVbox.lookup("#textOttava" + i);
						textOttava.setText(new Integer(strumentiTemp[i].getOttava()).toString());
						//forza
						TextField textForza = (TextField) dialogVbox.lookup("#textForza" + i);
						textForza.setText(new Integer(strumentiTemp[i].getForzaOn()).toString());
						//inizio
						TextField textInizio = (TextField) dialogVbox.lookup("#textInizio" + i);
						textInizio.setText(new Integer(strumentiTemp[i].getInizio()).toString());
						//continua per
						TextField textContinua = (TextField) dialogVbox.lookup("#textContinua" + i);
						textContinua.setText(new Integer(strumentiTemp[i].getContinuaPer()).toString());
						//ripeti per
						TextField textPausa = (TextField) dialogVbox.lookup("#textPausa" + i);
						textPausa.setText(new Integer(strumentiTemp[i].getPausa()).toString());
						//pausa
						TextField textRipetiPer = (TextField) dialogVbox.lookup("#textRipetiPer" + i);
						textRipetiPer.setText(new Integer(strumentiTemp[i].getRipetiPer()).toString());
						//ripeti per
						TextField textQuartina = (TextField) dialogVbox.lookup("#textQuartina" + i);
						textQuartina.setText(new Integer(strumentiTemp[i].getQuartina()).toString());
						//lunghezza nota statica
						TextField textLungNota = (TextField) dialogVbox.lookup("#textLungNota" + i);
						textLungNota.setText(new Double(strumentiTemp[i].getLunghezzaNota()).toString());
						//lunghezza nota dinamica
						TextField textLungGcg = (TextField) dialogVbox.lookup("#textLungGcg" + i);
						textLungGcg.setText(new Double(strumentiTemp[i].getLunghezzaGcg()).toString());
						//delay
						TextField textDelay = (TextField) dialogVbox.lookup("#textDelay" + i);
						textDelay.setText(new Double(strumentiTemp[i].getDelay()).toString());
						//Gap quartina
						TextField textGapQuartina = (TextField) dialogVbox.lookup("#textGapQuartina" + i);
						textGapQuartina.setText(new Integer(strumentiTemp[i].getGapQuartina()).toString());
						//repeat
						ComboBox<String> comboRepeat = (ComboBox) dialogVbox.lookup("#comboRepeat" + i);
						comboRepeat.setValue(strumentiTemp[i].getNomeRepeat());
					}
				}catch (Exception e1) {
					e1.printStackTrace();
					debug.setText(e1.getMessage());
				}
			}
		});
		sp.setContent(dialogVbox);
		Scene dialogScene = new Scene(sp, 1052.0, 650.0);
		dialog.setScene(dialogScene);
		dialog.show();				//mostra la finestra creata
	}
	
	private void changeState(int state){	//attiva o disattiva i pulsanti in base allo stato in input
		switch(state){
			case 0:
				setAllDisabled(false);
				stopButton.setDisable(true);
				resetButton.setDisable(true);
				playButton.setDisable(true);
				pauseMusicButton.setDisable(true);
				stopMusicButton.setDisable(true);
				break;
			case 1: case 5:
				setAllDisabled(true);
				stopButton.setDisable(false);
				break;
			case 2:
				setAllDisabled(false);
				stopButton.setDisable(true);
				pauseMusicButton.setDisable(true);
				stopMusicButton.setDisable(true);
				break;
			case 3:
				setAllDisabled(true);
				pauseMusicButton.setDisable(false);
				stopMusicButton.setDisable(false);
				break;
			case 4:
				setAllDisabled(false);
				stopButton.setDisable(true);
				pauseMusicButton.setDisable(true);
				break;
			default:
				setAllDisabled(false);
				break;
		}
	}
	
	private void setAllDisabled(boolean bool) {		//attiva o disattiva tutti i pulsanti in base all'input
		reinitializeButton.setDisable(bool);
		runButton.setDisable(bool);
		playButton.setDisable(bool);
		stopMusicButton.setDisable(bool);
		pauseMusicButton.setDisable(bool);
		stopButton.setDisable(bool);
		resetButton.setDisable(bool);
		settingsButton.setDisable(bool);
	}

	private void createBoardGof(int i, int size, double prob) {	//crea nuovo Game of Life
		gofBoards[i] = new GofBoard(size, size, prob);
	}

	private void createBoardGcg(int i, int size, int p) {		//crea nuovo Gcg
		gcgBoards[i] = new GcgBoard(size, size, p);
		//createDisplay();
	}

	private void createDisplay() {			//crea il display in base agli automi in input
		display = new JavaFXDisplayDriver(gofBoards[0].getSize(), cellSizePx, gofBoards[0], gcgBoards[0], gofBoards[1], gcgBoards[1], gofBoards[2], gcgBoards[2], gofBoards[3], gcgBoards[3]);

		baseGof.getChildren().clear();
		baseGof.getChildren().add(new Group(display.getPaneGof()));
		baseGcg.getChildren().clear();
		baseGcg.getChildren().add(new Group(display.getPaneGcg()));
		baseGof1.getChildren().clear();
		baseGof1.getChildren().add(new Group(display.getPaneGof1()));
		baseGcg1.getChildren().clear();
		baseGcg1.getChildren().add(new Group(display.getPaneGcg1()));
		baseGof2.getChildren().clear();
		baseGof2.getChildren().add(new Group(display.getPaneGof2()));
		baseGcg2.getChildren().clear();
		baseGcg2.getChildren().add(new Group(display.getPaneGcg2()));
		baseGof3.getChildren().clear();
		baseGof3.getChildren().add(new Group(display.getPaneGof3()));
		baseGcg3.getChildren().clear();
		baseGcg3.getChildren().add(new Group(display.getPaneGcg3()));
	}
}