#!/usr/bin/env python3

"""
Don't forget to install tk first!
yay -S tk
"""

import sqlite3
import tkinter as tk
from tkinter.scrolledtext import ScrolledText
import sys
import textwrap

conn = sqlite3.connect(sys.argv[1])
c = conn.cursor()
pending = c.execute("SELECT id, author, content FROM pasta WHERE approved = 0")


class Application(tk.Frame):
    def __init__(self, master=None):
        super().__init__(master)
        self.master = master
        self.pack()
        self.create_widgets()
        self.to_approve = []
        self.to_delete = []

    def create_widgets(self):
        self.button_approve = tk.Button(self, text="Approve", command=self.approve)
        self.button_approve.pack(side="top")

        self.button_delete = tk.Button(self, text="Delete", command=self.delete)
        self.button_delete.pack(side="top")

        self.button_submit = tk.Button(self, text="Submit", command=self.submit)
        self.button_submit.pack(side="bottom")

        self.next_pasta()

    def approve(self):
        self.to_approve.append(str(self.current_data[0]))
        self.next_pasta()

    def delete(self):
        self.to_delete.append(str(self.current_data[0]))
        self.next_pasta()

    def submit(self):
        c.executemany("UPDATE pasta SET approved = 1 WHERE id = ?", self.to_approve)
        c.executemany("DELETE FROM pasta WHERE id = ?", self.to_delete)
        conn.commit()

    def next_pasta(self):
        if hasattr(self, "pasta_content"):
            self.pasta_content.destroy()
        self.current_data = pending.fetchone()
        text = textwrap.fill(self.current_data[2], replace_whitespace=False)
        self.pasta_content = ScrolledText(self)
        self.pasta_content.tag_configure("tag-right", justify="right")
        self.pasta_content.insert(tk.END, text, "tag-right")
        self.pasta_content.pack(side="top")


root = tk.Tk()
app = Application(master=root)
app.mainloop()
