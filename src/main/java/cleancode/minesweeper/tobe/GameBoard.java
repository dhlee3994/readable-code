package cleancode.minesweeper.tobe;

import java.util.Arrays;
import java.util.Random;

import cleancode.minesweeper.tobe.cell.Cell;
import cleancode.minesweeper.tobe.cell.EmptyCell;
import cleancode.minesweeper.tobe.cell.LandMineCell;
import cleancode.minesweeper.tobe.cell.NumberCell;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;

public class GameBoard {

	private final Cell[][] board;
	private final int landMineCount;

	public GameBoard(GameLevel gameLevel) {
		this.board = new Cell[gameLevel.getRowSize()][gameLevel.getColSize()];
		this.landMineCount = gameLevel.getLandMineCount();
	}

	public void flag(int rowIndex, int colIndex) {
		Cell cell = findCell(rowIndex, colIndex);
		cell.flag();
	}

	public void open(int rowIndex, int colIndex) {
		Cell cell = findCell(rowIndex, colIndex);
		cell.open();
	}

	public void openSurroundedLandMines(int row, int col) {
		if (row < 0 || row >= getRowSize() || col < 0 || col >= getColSize()) {
			return;
		}
		if (isOpenedCell(row, col)) {
			return;
		}
		if (isLandMineCell(row, col)) {
			return;
		}

		open(row, col);

		if (doesCellHaveLandMineCount(row, col)) {
			return;
		}

		openSurroundedLandMines(row - 1, col - 1);
		openSurroundedLandMines(row - 1, col);
		openSurroundedLandMines(row - 1, col + 1);
		openSurroundedLandMines(row, col - 1);
		openSurroundedLandMines(row, col + 1);
		openSurroundedLandMines(row + 1, col - 1);
		openSurroundedLandMines(row + 1, col);
		openSurroundedLandMines(row + 1, col + 1);
	}

	private boolean doesCellHaveLandMineCount(int row, int col) {
		return findCell(row, col).hasLandMineCount();
	}

	private boolean isOpenedCell(int row, int col) {
		return findCell(row, col).isOpened();
	}

	public boolean isAllCellChecked() {
		return Arrays.stream(board)
			.flatMap(Arrays::stream)
			.allMatch(Cell::isChecked);
	}

	public boolean isLandMineCell(int selectedRowIndex, int selectedColIndex) {
		Cell cell = findCell(selectedRowIndex, selectedColIndex);
		return cell.isLandMine();
	}

	public void initializeGame() {
		int rowSize = getRowSize();
		int colSize = getColSize();

		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				board[row][col] = new EmptyCell();
			}
		}

		for (int i = 0; i < this.landMineCount; i++) {
			int landMineCol = new Random().nextInt(colSize);
			int landMineRow = new Random().nextInt(rowSize);
			board[landMineRow][landMineCol] = new LandMineCell();
		}

		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				if (isLandMineCell(row, col)) {
					continue;
				}
				int count = countNearbyLandMines(row, col);
				if (count == 0) {
					continue;
				}
				board[row][col] = new NumberCell(count);
			}
		}
	}

	public String getSign(int rowIndex, int colIndex) {
		return findCell(rowIndex, colIndex).getSign();
	}

	private Cell findCell(int rowIndex, int colIndex) {
		return board[rowIndex][colIndex];
	}

	public int getRowSize() {
		return this.board.length;
	}

	public int getColSize() {
		return this.board[0].length;
	}

	private int countNearbyLandMines(int row, int col) {
		int rowSize = getRowSize();
		int colSize = getColSize();

		int count = 0;
		if (row - 1 >= 0 && col - 1 >= 0 && isLandMineCell(row - 1, col - 1)) {
			count++;
		}
		if (row - 1 >= 0 && isLandMineCell(row - 1, col)) {
			count++;
		}
		if (row - 1 >= 0 && col + 1 < colSize && isLandMineCell(row - 1, col + 1)) {
			count++;
		}
		if (col - 1 >= 0 && isLandMineCell(row, col - 1)) {
			count++;
		}
		if (col + 1 < colSize && isLandMineCell(row, col + 1)) {
			count++;
		}
		if (row + 1 < rowSize && col - 1 >= 0 && isLandMineCell(row + 1, col - 1)) {
			count++;
		}
		if (row + 1 < rowSize && isLandMineCell(row + 1, col)) {
			count++;
		}
		if (row + 1 < rowSize && col + 1 < colSize && isLandMineCell(row + 1, col + 1)) {
			count++;
		}
		return count;
	}

}
