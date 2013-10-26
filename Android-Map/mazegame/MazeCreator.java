package com.jforeach.mazegame;

import com.jforeach.map.ShareResource;

public class MazeCreator {
	
	static int n = ShareResource.maxSizeX;
	static int m = ShareResource.maxSizeY;
	public static Maze getMaze() {
		Maze maze = null;
		
		boolean[][] vLines = new boolean[n][m];
		boolean[][] hLines = new boolean[n][m];
		maze = new Maze();
		for (int i = 0; i<n; i++)
			for (int j = 0; j<m; j++)
			{
				vLines[i][j] = true;
				hLines[i][j] = true;
			}
		maze.setVerticalLines(vLines);
		maze.setHorizontalLines(hLines);
		maze.setStartPosition(0, 0);
		maze.setFinalPosition(n - 1, m - 1);
		return maze;
//		
	}
}
