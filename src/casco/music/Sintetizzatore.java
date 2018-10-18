package casco.music;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

public class Sintetizzatore {
	private Synthesizer synthesizer;
	private final MidiChannel[] midiChannels;
	HashMap<String, Integer> mappaStrumenti;
	HashMap<String, Integer> mappaPercussioni;
	
	public Sintetizzatore(ArrayList<Strumento> orchestra) throws InvalidMidiDataException, Exception {
		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
		} catch (MidiUnavailableException ex) {
			ex.printStackTrace();
			System.exit(1);
		}   

		this.midiChannels = synthesizer.getChannels();
		
		//carica banca degli strumenti da file
		Soundbank bank = MidiSystem.getSoundbank(getClass().getResource("fluid.sf2"));

//		Soundbank bank = synthesizer.getDefaultSoundbank();
		synthesizer.loadAllInstruments(bank);
		synthesizer.loadAllInstruments(synthesizer.getDefaultSoundbank());

		orchestra.sort(null);
		//ad ogni canale viene associato uno strumento
		for(int i=0; i < orchestra.size() && i < synthesizer.getChannels().length; i++){
			midiChannels[i].programChange(orchestra.get(i).getStrumentIndex());
		}
		
		creaMappaStrumenti();
		//System.out.println("[STATE] MIDI channels: " + midiChannels.length);
		//System.out.println("[STATE] Instruments: " + instruments.length);
	}
	
	public void changeChannel(int i, int strumentIndex){
		midiChannels[i].programChange(strumentIndex);
	}
	
	//legge i flow dei vari spartiti e esegue noteOn e noteOff in base ai valori letti
	public void playFlowOnce(Spartito[] spartiti, int bpm, int time) throws InterruptedException {
//		if(time % quartina == 0)
//			System.out.print("\n" + time + "->Q:\t");
//		else
//			System.out.print("\n" + time + "->\t");
		for(int k = 0; k < spartiti.length; k ++){
			ArrayList<int[]> beat = spartiti[k].getFlow().get(time);
			if(beat != null) {
				for(int j = 0; j< beat.size();j++) {
					int array[] = beat.get(j);
					if(array[0] >= 0){
						midiChannels[array[1]].noteOn(array[0], spartiti[k].getStrumento().getForzaOn());
//						System.out.print("Channel[" + array[1] + "](" + spartiti[k].getStrumento().getName() + ").noteON:" + array[0] + ";  ");
					}
					else{
						midiChannels[array[1]]. noteOff(- array[0], spartiti[k].getStrumento().getForzaOn());
//						System.out.print("Channel[" + array[1] + "](" + spartiti[k].getStrumento().getName() + ").noteOFF:" + (-array[0]) + ";  ");
					}
				}
			}//else
//				System.out.print("X");
		}
	}
	
	public void stopMusic(){	//chiude tutte le note aperte
		for (int i = 0; i < midiChannels.length; i++)
			midiChannels[i].allNotesOff();
	}
	
	private void creaMappaStrumenti(){
		mappaStrumenti = new HashMap<String, Integer>();
		Instrument[] inst = synthesizer.getAvailableInstruments();
		String key;
		for(int i = 0; i < inst.length && i < 128; i++){
			key = inst[i].getName().trim();
			mappaStrumenti.put(key, new Integer(i));
			System.out.println("MAPPA  name: " + key + " index: " + mappaStrumenti.get(inst[i].getName()));
		}
		
		mappaPercussioni = new HashMap<String, Integer>();
		mappaPercussioni.put("Acoustic Bass Drum", new Integer(35));
		mappaPercussioni.put("Bass Drum 1", new Integer(36));
		mappaPercussioni.put("Side Stick", new Integer(37));
		mappaPercussioni.put("Acoustic Snare", new Integer(38));
		mappaPercussioni.put("Hand Clap", new Integer(39));
		mappaPercussioni.put("Electric Snare", new Integer(40));
		mappaPercussioni.put("Low Floor Tom", new Integer(41));
		mappaPercussioni.put("Closed Hi Hat", new Integer(42));
		mappaPercussioni.put("High Floor Tom", new Integer(43));
		mappaPercussioni.put("Pedal Hi-Hat", new Integer(44));
		mappaPercussioni.put("Low Tom", new Integer(45));
		mappaPercussioni.put("Open Hi-Hat", new Integer(46));
		mappaPercussioni.put("Low-Mid Tom", new Integer(47));
		mappaPercussioni.put("Hi-Mid Tom", new Integer(48));
		mappaPercussioni.put("Crash Cymbal 1", new Integer(49));
		mappaPercussioni.put("High Tom", new Integer(50));
		mappaPercussioni.put("Ride Cymbal 1", new Integer(51));
		mappaPercussioni.put("Chinese Cymbal", new Integer(52));
		mappaPercussioni.put("Ride Bell", new Integer(53));
		mappaPercussioni.put("Tambourine", new Integer(54));
		mappaPercussioni.put("Splash Cymbal", new Integer(55));
		mappaPercussioni.put("Cowbell", new Integer(56));
		mappaPercussioni.put("Crash Cymbal 2", new Integer(57));
		mappaPercussioni.put("Vibraslap", new Integer(58));
		mappaPercussioni.put("Ride Cymbal 2", new Integer(59));
		mappaPercussioni.put("Hi Bongo", new Integer(60));
		mappaPercussioni.put("Low Bongo", new Integer(61));
		mappaPercussioni.put("Mute Hi Conga", new Integer(62));
		mappaPercussioni.put("Open Hi Conga", new Integer(63));
		mappaPercussioni.put("Low Conga", new Integer(64));
		mappaPercussioni.put("High Timbale", new Integer(65));
		mappaPercussioni.put("Low Timbale", new Integer(66));
		mappaPercussioni.put("High Agogo", new Integer(67));
		mappaPercussioni.put("Low Agogo", new Integer(68));
		mappaPercussioni.put("Cabasa", new Integer(69));
		mappaPercussioni.put("Maracas", new Integer(70));
		mappaPercussioni.put("Short Whistle", new Integer(71));
		mappaPercussioni.put("Long Whistle", new Integer(72));
		mappaPercussioni.put("Short Guiro", new Integer(73));
		mappaPercussioni.put("Long Guiro", new Integer(74));
		mappaPercussioni.put("Claves", new Integer(75));
		mappaPercussioni.put("Hi Wood Block", new Integer(76));
		mappaPercussioni.put("Low Wood Block", new Integer(77));
		mappaPercussioni.put("Mute Cuica", new Integer(78));
		mappaPercussioni.put("Open Cuica", new Integer(79));
		mappaPercussioni.put("Mute Triangle", new Integer(80));
		mappaPercussioni.put("Open Triangle", new Integer(81));
	}
	
	public HashMap<String, Integer> listOfInstruments(){
		return mappaStrumenti;
	}
	public HashMap<String, Integer> listOfPercussions(){
		return mappaPercussioni;
	}
	
}
