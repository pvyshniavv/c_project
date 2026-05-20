#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
binary="$repo_root/bin/planar_graph"
input_dir="$repo_root/tests/in"
output_dir="$repo_root/tests/out"

mkdir -p "$output_dir"
rm -f "$output_dir"/*.txt

if [[ ! -x "$binary" ]]; then
  echo "Missing binary: $binary" >&2
  echo "Run 'make build' first." >&2
  exit 1
fi

run_case() {
  local input_file="$1"
  local base_name
  base_name="$(basename "$input_file" .txt)"
  local output_file="$output_dir/${base_name}.txt"

  "$binary" -i "$input_file" -o "$output_dir" -n "$base_name" -h >/dev/null

  if [[ ! -f "$output_file" ]]; then
    echo "FAIL: missing output for $base_name" >&2
    exit 1
  fi

  local expected_count actual_count
  expected_count="$(awk '{if ($2 > max) max = $2; if ($3 > max) max = $3} END {print max + 0}' "$input_file")"
  actual_count="$(head -n 1 "$output_file")"

  if [[ "$actual_count" != "$expected_count" ]]; then
    echo "FAIL: $base_name expected vertex count $expected_count, got $actual_count" >&2
    exit 1
  fi

  echo "PASS: $base_name"
}

for case_file in "$input_dir"/*.txt; do
  run_case "$case_file"
done
