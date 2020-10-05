package cz.refresh.badminton;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

public class Distributor {
	int players;
	int maxSameDoubles;
	int maxSameDoublesCount;
	int matchesCount;
	List<Match> matches;
	int[] frequencies;
	int maxIterations;
	private int smdevlimit =  20000; //naky parametry
	private int smcntlimtit = 10;
	
	
	public Distributor(int players, int  matchesCount,int maxSameDoubles,int maxSameDoublesCount,int  maxIterations ){
	 this.players = players;
	 this.matchesCount = matchesCount;
	 this.maxIterations = maxIterations;
	 this.frequencies = new int[players];
	 this.matches = new ArrayList<Match>();
	 this.maxSameDoubles = maxSameDoubles;
	 this.maxSameDoublesCount = maxSameDoublesCount;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 4){
			System.out.println("Pouziti:\n");
			System.out.println("bad.jar pocet_hracu pocet_zapasu maximalne_stejnych_dvojic maximalni_pocet_maximalne_stjenych_dvojic:)");
			
		}else{
			Distributor d = new Distributor(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),10000000);
			d.calculate();
		}
	}
	
	public void fillInitialMatches(){
		for(int k = 0;k<players;k++){
			frequencies[k] = 0;
		}
		for(int i=0;i<matchesCount;i++){
			Match t = new Match(players);
			//System.out.println(t);
			matches.add(t);
			//cache frequencies
			frequencies[t.players[0]]++;
			frequencies[t.players[1]]++;
			frequencies[t.players[2]]++;
			frequencies[t.players[3]]++;
		}
	}
	
	public int getFrequenciesDeviation(){
		int sum = 0;
		int dev = 0;
		int avg = 0;
		for(int i:frequencies){
			sum+=i;
		}
		avg = sum/players;
		for(int i:frequencies){
			dev+=((avg-i)*(avg-i));
		}
		return (dev*1000)/players; //at to muze bejt integer
	}
	
	public void printFrequencies(){
		int[] freq = new int[players];
		
		for(Match m: matches){
			freq[m.players[0]]++;
			freq[m.players[1]]++;
			freq[m.players[2]]++;
			freq[m.players[3]]++;
		}
		
		for(int i=0;i<players;i++){
			System.out.println("Hrac "+(i+1)+" hraje "+freq[i]+" zapasu");
		}
	
	}
	
	public void iterate(){
		doTheMagic();
	}
	
	public void calculate(){
		int sameDevCnt = 0;
		int sameMatchCnt = 0;
		int predev = 0;
		int postdev = 0;
		int minDev = 100000;
		int[] sameDoubles;
		
		fillInitialMatches();
		for(int i=0;i<maxIterations;i++){
			if(i%100000 == 0){
				//System.out.print('.');
			}
			predev = getFrequenciesDeviation();
			iterate();
			postdev = getFrequenciesDeviation();

			if((predev < postdev+200) && (predev > postdev-200)){
				sameDevCnt++;		
			}else{
				sameDevCnt=0;		
			}
			
			if(minDev > postdev){
				minDev= postdev;
			//	System.out.println("new minimum = "+minDev);
				//printMatches();
			//	printFrequencies();
				
			}
			
			sameDoubles = getMaxSameDoubles(false);
			if(postdev == 0 && sameDoubles[0] <= maxSameDoubles && sameDoubles[1] <= maxSameDoublesCount){
				//System.out.println("Optimum nalezeno");
				printMatches("matches.xls");
				System.out.println("Maximalne spolu nejaka dvojice hraje "+sameDoubles[0]+" krat a takovejch dvojic je "+sameDoubles[1]+"\n");
				System.exit(0);
				break;
			}
			

			if(sameDevCnt > smdevlimit){
				System.out.println("new initial solution");
				if(sameMatchCnt > smcntlimtit){
					matchesCount++;
					//trosicka rekurze,abychom si usetrili praci
					System.out.println("restart");
					Distributor d = new Distributor(players,matchesCount,maxSameDoubles,maxSameDoublesCount,maxIterations);
					d.calculate();
					return;
				}else{
					sameMatchCnt++;
				}
				
				
				fillInitialMatches();
			}			
		}
		//printMatches();
	}
	
	public void printMatches() {
		System.out.println(matches.toString());
		System.out.println("Rozptyl = "+getFrequenciesDeviation());
		for(int i=0;i<players;i++){
			System.out.println("Hrac "+(i+1)+" hraje "+frequencies[i]+" zapasu");
		}
		
	}

	public void printMatches(String fileName) {
		try{
		    // Create file
			int[] sameDoubles  = getMaxSameDoubles(false);
		    FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    
		
		
		
			out.write("<html>");
			out.write("<table>");
			out.write("<tr><td>Hrac 1</td><td>Hrac 2</td><td>Hrac 3</td><td>Hrac 4</td></tr>");
			for(Match m:matches){
				out.write("<tr>");
				out.write("<td>"+m.players[0]+"</td><td>"+m.players[1]+"</td><td>"+m.players[2]+"</td><td>"+m.players[3]+"</td>");
				out.write("</tr>");
			}
			out.write("<tr></tr>");
			out.write("<tr></tr>");
			
			out.write("<tr>");
			out.write("<td>Rozptyl</td><td>"+getFrequenciesDeviation()+"</td>");
			out.write("</tr>");
			out.write("<tr></tr>");
			
			
			
			out.write("<tr>");
			out.write("<td colspan=\"4\">Maximalni pocet stejnych dvojic </td><td>"+sameDoubles[0]+", "+sameDoubles[1]+" krat</td>");
			out.write("</tr>");
			out.write("<tr></tr>");
			out.write("<tr>");
			for(int i=0;i<players;i++){
				out.write("<tr>");
				out.write("<td colspan=\"4\">Hrac "+(i+1)+" hraje "+frequencies[i]+" zapasu</td>");
				out.write("</tr>");
			}
			out.write("</tr>");
			out.write("</table>");
			out.write("</html>");
			out.close();
		}catch (Exception e){//Catch exception if any
		      System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void doTheMagic(){
		int[] mostFrequent = new int[players];
		int[] leastFrequent = new int[players];
		int maxFreq = 0;
		int minFreq = matchesCount;
		int mfc = 0; //count
		int lfc = 0;
		
		
		for(int i=0;i<players;i++){
			if(frequencies[i]>maxFreq) maxFreq = frequencies[i];
			if(frequencies[i]<minFreq) minFreq = frequencies[i];
		}

		for(int i=0;i<players;i++){
			if(frequencies[i] == maxFreq){
				mostFrequent[mfc++] = i;
			}
			if(frequencies[i] == minFreq){
				leastFrequent[lfc++] = i;
			}
		}
		
		
		//ted nahodnyho z nejcetnejsich nahradime za nahradniho z nejmin cetnejch
		int lfi = (int) Math.round(Math.random()*(lfc-1));
		int mfi = (int) Math.round(Math.random()*(mfc-1));
		
		int mf = 0;
		try{
			mf = mostFrequent[mfi];
		}catch (ArrayIndexOutOfBoundsException e){
			for(int j=0;j<mfc;j++){
				System.out.println(mostFrequent[j]);
			}
		}
		
		int lf = 0;
		try{
			lf = leastFrequent[lfi];
		}catch (ArrayIndexOutOfBoundsException e){
			for(int j=0;j<lfc;j++){
				System.out.println(leastFrequent[j]);
			}
		}
		
		Match m;
		int[] containing = new int[matches.size()];
		int containingCnt = 0;
		
		for(int i=0;i<matches.size();i++){
			m=matches.get(i); 
			
			if(m.contains(mf)){
				containing[containingCnt++] = i;
			}
		}
		
		Match n = matches.get(containing[(int) Math.round(Math.random()*(containingCnt-1)) ]);
		if(!n.contains(mf)){
			System.out.println(containingCnt);
			System.out.println("ouch");
		
		}
		int with = n.replacePlayer(mf, lf);
		if(!n.isValid()){
			System.out.println("not valid Replacing "+mf+" with "+lf+" "+" real "+with +" "+n);
			System.exit(1);
		}
		
		frequencies[with]++;
		frequencies[mf]--;
	}
	
	public int[] getMaxSameDoubles(boolean print){
		int[][] doubles = new int[players][players];
		int maxSameDoubles = 0;
		int sameDoublesCount = 0;
		
		for(Match m: matches){
			doubles[Math.min(m.players[0], m.players[1])][Math.max(m.players[0], m.players[1])]++;
			doubles[Math.min(m.players[2], m.players[3])][Math.max(m.players[2], m.players[3])]++;
		}
		
		//vypsat nejcetnejsi dvojice
		
		for(int i=0;i<players;i++ ){
			for(int j=0;j<players;j++ ){

				if(doubles[i][j] > maxSameDoubles){
					sameDoublesCount=0;
				}

				if(doubles[i][j] > 0){
					if(print) System.out.println("Hrac "+i+" s Hracem "+j+" "+doubles[i][j]+" krat.");
					if(maxSameDoubles < doubles[i][j]){
						maxSameDoubles = doubles[i][j];
					}
				}
				if(doubles[i][j]== maxSameDoubles){
					sameDoublesCount++;
				}
			}
		}		
		int[] ret ={maxSameDoubles, sameDoublesCount}; 
		return ret;
	}
}