package me.cxom.jailbreak3.game;

public interface PvpGame {
	
	public GameState getGameState();
	
//	default public boolean isAvailable() {
//		return getGameState() == GameState.WAITING;
//	}
	
	default public boolean isStopped() {
		return getGameState() == GameState.STOPPED;
	}
	
	default public boolean isWaiting() {
		return getGameState() == GameState.WAITING;
	}
	
//	public Arena getArena();
	
}
