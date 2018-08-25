package cz.refresh.badminton;


public class Match {
	public int[] players;
	int maxPlayers;
	/**
	 * 	 * @param maxPlayers
	 *  nahodne nainicializuje match, tak aby tam nebyli stejni hraci
	 */
	
	public Match(int maxPlayers){
		players = new int[4];
		this.maxPlayers = maxPlayers;
		int starter =(int) Math.round(Math.random()*(maxPlayers-1));
		players[0] = starter;
		while(true){
			players[1] = (int) Math.round(Math.random()*(maxPlayers-1));
			if(players[1] != players[0]){
				break;
			}
		}
		while(true){
			players[2] = (int) Math.round(Math.random()*(maxPlayers-1));
			if(players[2] != players[0] && players[2] != players[1]){
				break;
			}
		}
		while(true){
			players[3] = (int) Math.round(Math.random()*(maxPlayers-1));
			if(players[3] != players[0] && players[3] != players[1] && players[3] != players[2]){
				break;
			}
		}
	}
	
	public String toString(){
		return "Match "+players[0]+" a "+players[1]+" vs. "+players[2]+" a "+players[3]+"\n";
	}
	
	public boolean isValid(){
		return players[0]!=players[1] &&
				players[0]!=players[2]&&
				players[0]!=players[3]&&
				players[1]!=players[2]&&
				players[1]!=players[3]&&
				players[2]!=players[3];
	
	}
	
	public int replacePlayer(int who){
		return replacePlayer(who,getRandomNotInMatch());
	}
	
	private int getRandomNotInMatch() {
		/*doufam ze nekdy skoncime :) nevhodne pro zapasy kde jsou ctyri a min hracu*/
		while(true){
			int r =(int) Math.round(Math.random()*(maxPlayers-1));
			if(
			players[0] != r &&		
			players[1] != r &&		
			players[2] != r &&		
			players[3] != r		
			){
				return r;
			}
		}
	}

	public int replacePlayer(int who, int with){
		for(int i=0;i<4; i++) {
			if(players[i] == who){
				players[i] = with;
				if(!isValid()){
					return replacePlayer(with);
				}else{
					return with;
				}
			}
		}
		System.out.println("OMG "+who+" "+with+" "+this);
		return -999;
	}

	public boolean contains(int mf) {
		return (
				players[0] == mf ||		
				players[1] == mf ||		
				players[2] == mf ||		
				players[3] == mf		
				);
	}

}
