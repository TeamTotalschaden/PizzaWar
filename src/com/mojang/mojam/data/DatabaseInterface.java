package com.mojang.mojam.data;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DatabaseInterface extends Remote {
	public List<ScoreEntry> getScore(long start) throws RemoteException;

	public long getPageCount(long pageSize) throws RemoteException;

	public void submitScore(long score, String name) throws RemoteException;
}
