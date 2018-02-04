package com.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {
	public final int DIAGONAL_COST = 14;
	public final int V_H_COST = 10;
	public int nextX, nextY, x, y;
	public static List<List<Integer>> returnPathArray = new ArrayList<List<Integer>>();
	
	// Blocked cells are just null Cell values in grid
	Cell[][] grid;
	PriorityQueue<Cell> open;
	boolean closed[][];
	int startI, startJ;
	int endI, endJ;

	class Cell {
		int heuristicCost = 0; // Heuristic cost
		int finalCost = 0; // G+H
		int i, j;
		Cell parent;

		Cell(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}

	public AStar() {

	}

	public void setBlocked(int i, int j) {
		if ((i < x) && (j < y)) {
			grid[i][j] = null;
		}
	}

	public void setStartCell(int i, int j) {
		startI = i;
		startJ = j;
	}

	public void setEndCell(int i, int j) {
		endI = i;
		endJ = j;
	}

	public List<List<Integer>> getPathArray(){
		return returnPathArray;
	}

	public void checkAndUpdateCost(Cell current, Cell t, int cost) {
		if (t == null || closed[t.i][t.j])
			return;
		int t_final_cost = t.heuristicCost + cost;

		boolean inOpen = open.contains(t);
		if (!inOpen || t_final_cost < t.finalCost) {
			t.finalCost = t_final_cost;
			t.parent = current;
			if (!inOpen)
				open.add(t);
		}
	}

	public void compute() {

		// add the start location to open list.
		if (grid[startI][startJ] == null) {
			grid[startI][startJ] = new Cell(startI, startJ);
		}
		
		open.add(grid[startI][startJ]);

		Cell current;

		while (true) {
			current = open.poll();
			if (current == null)
				break;
			closed[current.i][current.j] = true;

			if (current.equals(grid[endI][endJ])) {
				return;
			}

			Cell t;
			if (current.i - 1 >= 0) {
				t = grid[current.i - 1][current.j];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);

				if (current.j - 1 >= 0) {
					t = grid[current.i - 1][current.j - 1];
					checkAndUpdateCost(current, t, current.finalCost + DIAGONAL_COST);
				}

				if (current.j + 1 < grid[0].length) {
					t = grid[current.i - 1][current.j + 1];
					checkAndUpdateCost(current, t, current.finalCost + DIAGONAL_COST);
				}
			}

			if (current.j - 1 >= 0) {
				t = grid[current.i][current.j - 1];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
			}

			if (current.j + 1 < grid[0].length) {
				t = grid[current.i][current.j + 1];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
			}

			if (current.i + 1 < grid.length) {
				t = grid[current.i + 1][current.j];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);

				if (current.j - 1 >= 0) {
					t = grid[current.i + 1][current.j - 1];
					checkAndUpdateCost(current, t, current.finalCost + DIAGONAL_COST);
				}

				if (current.j + 1 < grid[0].length) {
					t = grid[current.i + 1][current.j + 1];
					checkAndUpdateCost(current, t, current.finalCost + DIAGONAL_COST);
				}
			}
		}
	}

	public void route(int constant, int x, int y, int sj, int si, int ej, int ei, List<List<Integer>> obstacleArray) {

		if ((x < sj) || (y < si)){
			si = si/2;
			sj = sj/2;
		}
		
		this.x = x;
		this.y = y;
		grid = new Cell[x][y];
		closed = new boolean[x][y];
		open = new PriorityQueue<Cell>(16, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Cell c1 = (Cell) o1;
				Cell c2 = (Cell) o2;
				return c1.finalCost < c2.finalCost ? -1 : c1.finalCost > c2.finalCost ? 1 : 0;
			}
		});
		
		setStartCell(si, sj);
		setEndCell(ei, ej);

		for (int i = 0; i < x; ++i) {
			for (int j = 0; j < y; ++j) {
				grid[i][j] = new Cell(i, j);
				grid[i][j].heuristicCost = Math.abs(i - endI) + Math.abs(j - endJ);
			}
		}

		grid[si][sj].finalCost = 0;

		for (int i = 0; i < obstacleArray.size(); ++i) {
			setBlocked((int) (obstacleArray.get(i)).get(1) / constant, (int) (obstacleArray.get(i)).get(0) / constant);
		}

		compute();

		if (closed[endI][endJ]) {
			List<List<Integer>> pathArray = new ArrayList<List<Integer>>();

			Cell current = grid[endI][endJ];
			current = current.parent;

			List<Integer> rowFirst = new ArrayList<Integer>();
			rowFirst.add(current.j);
			rowFirst.add(current.i);
			pathArray.add(rowFirst);

			while (current.parent != null) {

				List<Integer> row = new ArrayList<Integer>();
				row.add(current.j);
				row.add(current.i);
				pathArray.add(row);

				nextX = current.j;
				nextY = current.i;
				current = current.parent;
			}

			returnPathArray = pathArray;
			
		} else {
		}
	}
}