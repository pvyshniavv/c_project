.PHONY: all build clean help run test

CC := gcc
CFLAGS := -Wall -Wextra
LDLIBS := -lm

SRC_DIR := src
BIN_DIR := bin
BUILD_DIR := build
BUILD_STAMP := $(BUILD_DIR)/.dir
SOURCES = $(wildcard $(SRC_DIR)/*.c)
OBJECTS = $(SOURCES:$(SRC_DIR)/%.c=$(BUILD_DIR)/%.o)
TARGET = $(BIN_DIR)/planar_graph

# Default target
all: $(TARGET)

# Create directories
$(BUILD_STAMP):
	@mkdir -p $(BUILD_DIR)
	@touch $@

$(BIN_DIR):
	@mkdir -p $@

# Compile object files
$(BUILD_DIR)/%.o: $(SRC_DIR)/%.c | $(BUILD_STAMP)
	$(CC) $(CFLAGS) -c $< -o $@

# Link executable
$(TARGET): $(OBJECTS) | $(BIN_DIR)
	$(CC) $(OBJECTS) $(LDFLAGS) $(LDLIBS) -o $@
	@echo "Build complete: $@"

# Alias for all
build: $(TARGET)

# Run the application
run: $(TARGET)
	./$(TARGET)

# Run planar graph test cases
test: $(TARGET)
	@chmod +x tests/run_planar_tests.sh
	@tests/run_planar_tests.sh

# Clean build artifacts
clean:
	@rm -rf $(BUILD_DIR) $(BIN_DIR)
	@echo "Clean complete"

# Display help
help:
	@echo "Planar Graph Visualization - Available targets:"
	@echo "  make build    - Compile the project"
	@echo "  make run      - Build and run the application"
	@echo "  make clean    - Remove build artifacts"
	@echo "  make help     - Display this help message"
