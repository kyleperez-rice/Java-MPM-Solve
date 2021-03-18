# Contains global variables that apply to all my python programs here
# See solver_options.h for the values too!

# Geometry
num_nodes = 201

xlowbnd = 0.
xhighbnd = 1.

dx = (xhighbnd - xlowbnd)/(num_nodes-1)


# Particle properties
num_mps = 2



# Time
tmax = 0.3
tsteps = 3000

dt = tmax/tsteps