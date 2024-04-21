import glob
import graphviz

for path in glob.iglob("*.dot"):
    print(f"Rendering {path}...")

    graphviz.render("fdp", "png", path, outfile=f"img/{path}.png")
