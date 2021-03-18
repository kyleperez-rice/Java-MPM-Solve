import math
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import numpy as np

from plot_constants import *


mesh_data = pd.read_csv(r'/home/kyleperez/javastuff/admpm/test.csv', header=0)


def analytic_solution(type, x, t):
# Analytic solution for u(x,t) = -A * sin(pi x / L) * sin(pi c t / L)

	rho = 1.
	Y = 1.

	c = np.sqrt(Y/rho)

	L = 1.

	A = 0.01

	omega = 10.

	y = []

	if (type == "Strain"):

		for i in range( len(x) ):


			if x[i] < (L - c*t):

				y.append(0.)

			else:

				y.append((A/c) * np.sin( omega*(x[i]-L)/c + omega*t ))

			#end if


		#end for

	elif (type == "Density"):

		for i in range( len(x) ):


			if x[i] < (L - c*t):

				y.append(rho)

			else:

				y.append(rho*( 1. - (A/c)*np.sin( omega*(x[i]-L)/c + omega*t ) ))

			#end if


		#end for

	elif (type == "Xvel"):

		for i in range( len(x) ):


			if x[i] < (L - c*t):

				y.append(0.)

			else:

				y.append( A* np.sin( omega*(x[i]-L)/c + omega*t ) )

			#end if


		#end for

	#end if


	return y


#end analytic_solution


"""
def analytic_solution(type, x, t):
# Analytic solution for u(x,t) = -A * sin(pi x / L) * sin(pi c t / L)

	rho = 1.
	Y = 1.

	c = np.sqrt(Y/rho)

	L = 1.

	A = 0.01

	if (type == "Strain"):

		return -A*(np.pi/L) * np.cos(np.pi * x/L) * np.sin( c* np.pi * t/L)

	elif (type == "Density"):
		# dens = rho( 1 - strain )

		data = rho * np.ones( len(x) )

		return data + A*rho*(np.pi/L) * np.cos(np.pi * x/L) * np.sin( c* np.pi * t/L)

	elif (type == "Xvel"):

		return -c * A* (np.pi / L) * np.sin(np.pi * x / L) * np.cos(c * np.pi * t / L) 

	#end if


#end analytic_solution
"""







fig = plt.figure()
ax = fig.add_subplot(1,1,1)


#Number of nodes
# Imported as num_nodes



def animation(i):
	x = np.array(mesh_data['xpos'][num_nodes*i : num_nodes*(i+1)])
	
	#y = mesh_data['strain'][num_nodes*i: num_nodes*(i+1)]
	y = mesh_data['dens'][num_nodes*i: num_nodes*(i+1)]
	#y = mesh_data['xvel'][num_nodes*i: num_nodes*(i+1)]

	time = i*dt
	
	ax.clear()
	ax.plot(x, y, '-', label='Simulation')

	#ax.plot(x, analytic_solution('Strain', x, time), '--', label='Analytic')
	ax.plot(x, analytic_solution('Density', x, time), '--', label='Analytic')
	#ax.plot(x, analytic_solution('Xvel', x, time), '--', label='Analytic')
	ax.legend()
	
	#Works for Stress/Strain
	#ax.set(ylim = [-0.015, 0.015], xlim=[xlowbnd,xhighbnd], xlabel='x (cm)', ylabel='Strain',title='Strain v x')
	
	#Works for Density
	ax.set(ylim=[0.985, 1.015], xlim=[xlowbnd,xhighbnd], xlabel='x (cm)', ylabel='Density (g/cm**3)',title='Density v x')
	
	#Works for xvel
	#ax.set(ylim=[-.011, .011], xlim=[xlowbnd,xhighbnd], xlabel='x (cm)', ylabel='Velocity (cm/s)',title='Velocity v x')
	
	
# the frames=... num can prevent the plot from crashing
animation = FuncAnimation(fig, func=animation, frames=tsteps, interval=10)




plt.show()
