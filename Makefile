CC = gcc
CFLAGS = -I include -Wall
TARGET = program.exe
SRC = src/*.c

$(TARGET): $(SRC)
	$(CC) $(CFLAGS) $(SRC) -o $(TARGET)

clean:
	rm -f $(TARGET)