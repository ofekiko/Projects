# ID: 322374240
# NAME: OFEK KAHARIZI
import tkinter as tk
from tkinter import messagebox, Button
import random

class Cell:
    def __init__(self, x: int, y: int, is_mine: bool = False):
        self.x = x
        self.y = y
        self.is_mine = is_mine
        self.is_revealed = False
        self.is_flagged = False
        self.neighbor_mines = 0

    def calculate_neighbor_mines(self, grid):
        directions = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]
        self.neighbor_mines = sum(
            1 for dx, dy in directions
            if
            0 <= self.x + dx < len(grid) and 0 <= self.y + dy < len(grid[0]) and grid[self.x + dx][self.y + dy].is_mine
        )

class Board:
    def __init__(self, size: int, mine_count: int, root: tk):
        self.size = size
        self.mine_count = mine_count
        self.grid = [[Cell(x, y) for y in range(size)] for x in range(size)]
        self.buttons = []
        self.unrevealed = size ** 2
        self._place_mines()
        self._calculate_all_neighbors()
        for i in range(size):
            buttons_row = []
            for j in range(size):
                button = Button(root, width=2, height=1, bg="gray")
                button.grid(row=i, column=j)
                button.bind("<Button-1>", self.reveal_cell)
                button.bind("<Button-3>", self.toggle_flag)
                buttons_row.append(button)
            self.buttons.append(buttons_row)

    def _place_mines(self):
        all_positions = [(i, j) for i in range(self.size) for j in range(self.size)]
        mine_positions = random.sample(all_positions, self.mine_count)
        for (i, j) in mine_positions:
            self.grid[i][j].is_mine = True

    def _calculate_all_neighbors(self):
        for row in self.grid:
            for cell in row:
                cell.calculate_neighbor_mines(self.grid)

    def reveal_cell(self, event=None):
        button = event.widget
        i = button.grid_info()['row']
        j = button.grid_info()['column']
        if 0 <= i < self.size and 0 <= j < self.size:
            cell = self.grid[i][j]
            if not cell.is_revealed and not cell.is_flagged:
                if cell.is_mine:
                    self.buttons[i][j].config(bg="red", state=tk.DISABLED, text="B")
                    messagebox.showinfo("Result", "you hit a bomb!")
                    quit()
                self.buttons[i][j].config(bg="white", state=tk.DISABLED, text=str(cell.neighbor_mines)
                if cell.neighbor_mines > 0 else "")
                self.unrevealed -= 1
                if self.unrevealed == self.mine_count:
                    messagebox.showinfo("Result", "you won the game!")
                    quit()
                cell.is_revealed = True
                if cell.neighbor_mines == 0 and not cell.is_mine:
                    self._reveal_neighbors(i, j)

    def _reveal_neighbors(self, x: int, y: int):
        directions = [(-1, -1), (-1, 0), (-1, 1), (0, -1), (0, 1), (1, -1), (1, 0), (1, 1)]
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            if 0 <= nx < self.size and 0 <= ny < self.size:
                neighbor = self.grid[nx][ny]
                if not neighbor.is_revealed and not neighbor.is_mine and not neighbor.is_flagged:
                    neighbor.is_revealed = True
                    self.buttons[nx][ny].config(bg="white", state=tk.DISABLED, text=str(neighbor.neighbor_mines)
                    if neighbor.neighbor_mines > 0 else "")
                    self.unrevealed -= 1
                    if neighbor.neighbor_mines == 0:
                        self._reveal_neighbors(nx, ny)

    def toggle_flag(self, event=None):
        button = event.widget
        i = button.grid_info()['row']
        j = button.grid_info()['column']
        if 0 <= i < self.size and 0 <= j < self.size:
            cell = self.grid[i][j]
            if not cell.is_revealed:
                cell.is_flagged = not cell.is_flagged
                if cell.is_flagged:
                    button.config(bg="yellow", text="F")
                else:
                    button.config(bg="gray", text=" ")

if __name__ == "__main__":
    valid_input = False
    grid_size = 0
    bombs = 0
    while not valid_input:
        try:
            grid_size = int(input("Enter the size of the board: "))
            bombs = int(input("Enter the number of bombs: "))
            if grid_size > 0 and bombs > 0:
                valid_input = True
            else:
                print("Please enter positive integers")
        except ValueError:
            print("invalid input. only integers")
    root1 = tk.Tk()
    board = Board(grid_size, bombs, root1)
    root1.title("Minesweeper")
    root1.mainloop()