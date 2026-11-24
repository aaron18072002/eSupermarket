---
name: powershell-response-standards
description: Enforces response formatting and terminal visual standards tailored for Windows PowerShell running the Antigravity CLI (agy). Guarantees zero-failure Mermaid (< 65 cols), terminal-safe Unicode box flowcharts, and PowerShell shell command conventions.
---

# 📟 PowerShell & `agy` CLI Response Standards

This skill defines the communication, visualization, and shell standards for interacting with users running the **Antigravity CLI (`agy`)** inside **Windows PowerShell**.

---

## 1. 🖥️ Runtime Environment Awareness

* The user interacts with Antigravity exclusively through **Windows PowerShell (`agy`)**.
* Standard PowerShell console buffers often operate between **80 and 120 columns** (or split panes), and may expand up to ~200 columns when maximized.
* To guarantee that all visual models render inline without clipping, line wrapping, or demoting to raw code blocks, all visualizations must be designed for **maximum width < 60–65 columns**.

---

## 2. 📊 Mandatory Dual Visualization Standard

Whenever explaining a flow, lifecycle, query, or architecture, **always provide two complementary models**:

1. **Terminal-Safe Unicode Box Flowchart (ASCII / Box Art):**
   * Primary visual model that renders 100% reliably in any PowerShell terminal.
   * Total width strictly under **60 columns**.
   * Use standard Unicode box-drawing characters: `┌`, `─`, `┐`, `│`, `└`, `┘`, `├`, `┤`, `┬`, `┴`, `┼`, `▼`, `►`.

2. **Inline-Safe Mermaid Diagram (`flowchart TD`):**
   * Optimized for environments that parse Mermaid graphically.
   * Rendered width must be strictly under **65 columns** so it never triggers the terminal overflow error:
     `Diagram exceeds terminal width (X > Y cols)`.

---

## 3. 🛡️ Zero-Failure Mermaid Sizing & Syntax Rules

To prevent Mermaid parser crashes or wide Dagre layout explosion in PowerShell:

### A. Strictly No Backward Edges (Cycles)
* **FORBIDDEN:** Routing an arrow from a lower node back up to an earlier node (e.g. `Step8 --> Step3`).
* **Why:** The Dagre layout engine calculates a wide bypass corridor around the entire diagram, instantly widening the diagram to 220+ columns and breaking PowerShell terminal display.
* **Solution:** Terminate failure paths into **local leaf nodes** (e.g. `Err1["Error Alert"]` and `Err2["Error Alert"]`).

### B. Vertical Orientation & Single-Track Happy Path
* Always use `flowchart TD` (top-down). Never use `LR` (left-to-right).
* Keep the main success path aligned in a straight vertical column down the center.
* Keep decision leaves minimal and immediate.

### C. Label Character Limits
* Maximum **18–22 characters** per line inside node labels.
* Use `<br/>` for line wraps instead of continuous long text.
* Always wrap all labels in double quotes: `NodeId["Label text"]`.

### D. Zero Unescaped Angle Brackets
* Strictly NEVER use raw `<...>` (e.g. `<AuthResponse>`, `<T>`, `<void>`) as Mermaid treats them as unclosed HTML tags and crashes.
* Use parentheses instead: `(AuthResponse)` or plain text.

---

## 4. ⌨️ PowerShell Command Conventions

When providing terminal commands to the user:
* Always provide valid **Windows PowerShell** syntax.
* **Environment variables:** Use `$env:PORT="8080"` (never `export PORT=8080`).
* **Path separators:** Windows backslashes `\` or forward slashes `/` supported by PowerShell.
* **Universal CLI tools:** Prefer `mvn`, `pnpm`, `git`, `npm`, and standard PowerShell cmdlets (`Get-ChildItem`, `Test-Path`).
