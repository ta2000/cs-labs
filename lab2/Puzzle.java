package sudoku;

import java.util.Random;
import javafx.application.*;
import java.util.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.layout.GridPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.animation.*;

public class Puzzle {
	public double x;
	public double y;
	public boolean solving = false;
	public int boxSize;
	public int[][] grid;
	public boolean[][] locked;
	int nextX;
	int nextY;

	public Puzzle(double x, double y, int boxSize) {
		this.x = x;
		this.y = y;
		this.nextX = 0;
		this.nextY = 0;
		this.boxSize = boxSize;
		this.grid = new int[9][9];
		// Prevent program from changing preset values
		this.locked = new boolean[9][9];
	}

	public void solve(int x, int y) {
		this.nextX++;
		if (!this.locked[x][y]) {
			boolean valid = false;
			while (!valid) {
				this.grid[x][y]++;
				valid = !this.columnContains(x, y) &&
						!this.rowContains(x, y) &&
						!this.squareContains(x, y);

				if (this.grid[x][y] > 9) {
					this.nextX-=2;
                    if (this.nextX < 0) {
                        this.nextX = 8;
                        this.nextY--;
                    }
					while (this.locked[this.nextX][this.nextY]) {
						this.nextX--;
						if (this.nextX < 0) {
							this.nextX = 8;
							this.nextY--;
						}
					}
					this.grid[x][y] = 0;
					break;
				}
			}
			//System.out.println("Buga");
		}
	}

	public boolean columnContains(int row, int column) {
		boolean contains = false;
		for (int i=0; i<9; i++) {
			if (i != column && this.grid[row][i] == grid[row][column]) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public boolean rowContains(int column, int row) {
		boolean contains = false;
		for (int i=0; i<9; i++) {
			if (i != column && this.grid[i][row] == grid[column][row]) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public boolean squareContains(int column, int row) {
		boolean contains = false;
		int squareX = (column/3)*3;
		int squareY = (row/3)*3;
		int value = this.grid[column][row];

		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if ( !((squareX+i)==column && (squareY+j)==row) ) {
					if (this.grid[squareX+i][squareY+j] == value)
						contains = true;
				}
			}
		}

		return contains;
	}

	public void changeCell(double x, double y, int amount) {
		// Disable editing while the solver is running
		if (solving)
			return;

		// Find cell from x/y position on canvas
		double roundX = Math.round((Math.floor((x - this.x)/this.boxSize)*this.boxSize)/this.boxSize);
		double roundY = Math.round((Math.floor((y - this.y)/this.boxSize)*this.boxSize)/this.boxSize);

		// Prevent array out of bounds
		if (roundX >= 9 || roundY >= 9 ||
			roundX < 0  || roundY < 0)
				return;

		// Change cell number by (amount), preventing negative numbers
		if (this.grid[(int)roundX][(int)roundY] + amount >= 0) {
			this.grid[(int)roundX][(int)roundY] += amount;
		// Wrap around below 0
		} else {
			this.grid[(int)roundX][(int)roundY] = 9;
		}

		// Wrap around above 9
		grid[(int)roundX][(int)roundY] %= 10;

		// Tell the program not to modify cells changed by the user
		if (this.grid[(int)roundX][(int)roundY] != 0)
			this.locked[(int)roundX][(int)roundY] = true;
		else
			this.locked[(int)roundX][(int)roundY] = false;
	}

	public void reset() {
        this.nextX = 0;
        this.nextY = 0;

		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				this.grid[i][j] = 0;
				this.locked[i][j] = false;
			}
		}
	}

	public void update() {
		if (solving) {
			if (this.nextX > 8) {
				this.nextY++;
				this.nextX = 0;
			} else if (this.nextX < 0) {
				this.nextX = 8;
				this.nextY--;
			}
			if (this.nextY > 8)
				this.solving = false;
			else
				solve(this.nextX, this.nextY);
		}
	}

	public void draw(GraphicsContext gc) {
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (i%3 == 0 && j%3 == 0)
				{
					gc.setLineWidth(5);
					gc.strokeRect(
						this.x + i*this.boxSize,
						this.y + j*this.boxSize,
						this.boxSize*3,
						this.boxSize*3
					);
				}

				gc.setLineWidth(1);
				gc.strokeRect(
					this.x + i*this.boxSize,
					this.y + j*this.boxSize,
					this.boxSize,
					this.boxSize
				);

				gc.setFont(Font.font("Verdana", 18));
				if (this.locked[i][j])
					gc.setFill(Color.RED);
				else
					gc.setFill(Color.BLACK);

				if (grid[i][j] > 0) {
					gc.fillText(
						Integer.toString(grid[i][j]),
						i*this.boxSize + this.x + this.boxSize/2 - 6,
						j*this.boxSize + this.y + this.boxSize/2 + 4
					);
				}
			}
		}

		gc.setFill(Color.web("0xFFFF00", 0.5));
		gc.fillRect(
			this.x + this.nextX * this.boxSize,
			this.y + this.nextY * this.boxSize,
			this.boxSize,
			this.boxSize
		);
	}
}
