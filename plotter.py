import math
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import animation
import numpy as np

from plot_constants import *


mesh_data = pd.read_csv(r'/home/kyleperez/javastuff/admpm/ShakeWave/Moving/test.csv', header=0)


# Analytic solution for a solid with a wave shaking on the right
def analytic_solution(type, x, t):


	# Initial Density/ Young's Modulus
	rho0 = 1.
	Y = 1.

	# Speed of sound
	c = np.sqrt(Y/rho0)

	# Length of the system
	L = 1.

	# Driving velocity
	v0 = 0.01

	# Angular frequency of drive
	omega = 10.

	y = []

	if (type == "Strain"):

		for i in range( len(x) ):


			if t < L/c:

				if x[i] < L-c*t:

					y.append(0.)

				else:

					y.append( v0 * np.sin( omega*( (x[i]-L)/c + t ) )/c )

				#end if

			elif t < 2*L/c:

				if x[i] < c*t-L:

					y.append( 2*v0 * np.cos(omega*x[i]/c) * np.sin(omega*(t-L/c))/c )

				else:

					y.append( v0 * np.sin( omega*( (x[i]-L)/c + t ) )/c )

				#end if

			else:

				y.append(0.)

			#end if


		#end for

	elif (type == "Density"):

		for i in range( len(x) ):


			if t < L/c:

				if x[i] < L-c*t:

					y.append( rho0 )

				else:

					y.append( rho0*( 1 -  v0 * np.sin( omega*( (x[i]-L)/c + t ) )/c) )

				#end if

			elif t < 2*L/c:

				if x[i] < c*t-L:

					y.append( rho0*( 1 - 2*v0 * np.cos(omega*x[i]/c) * np.sin(omega*(t-L/c))/c ) )

				else:

					y.append( rho0*( 1 -  v0 * np.sin( omega*( (x[i]-L)/c + t ) )/c) )

				#end if

			else:

				y.append(0.)

			#end if


		#end for

	elif (type == "Xvel"):

		for i in range( len(x) ):


			if t < L/c:

				if x[i] < L-c*t:

					y.append(0.)

				else:

					y.append( v0 * np.sin( omega*( (x[i]-L)/c + t ) ) )

				#end if

			elif t < 2*L/c:

				if x[i] < c*t - L:

					y.append( 2*v0 * np.sin(omega*x[i]/c) * np.cos(omega*(t-L/c)) )

				else:

					y.append( v0 * np.sin( omega*( (x[i]-L)/c + t ) ) )

				#end if

			else:

				y.append(0.)

			#end if


		#end for

	#end if


	return y


#end analytic_solution







fig = plt.figure()
ax = fig.add_subplot(1,1,1)


#Number of nodes
# Imported as num_nodes



def update(i):


	x = np.array(mesh_data['xpos'][num_nodes*i : num_nodes*(i+1)])
	
	y = mesh_data['strain'][num_nodes*i: num_nodes*(i+1)]
	#y = mesh_data['dens'][num_nodes*i: num_nodes*(i+1)]
	#y = mesh_data['xvel'][num_nodes*i: num_nodes*(i+1)]

	time = i*dt * record_frequency
	
	ax.clear()
	ax.plot(x, y, '-', label='Simulation')

	
	if ( time < 2. ):

		ax.plot(x, analytic_solution('Strain', x, time), '--', label='Analytic')
		#ax.plot(x, analytic_solution('Density', x, time), '--', label='Analytic')
		#ax.plot(x, analytic_solution('Xvel', x, time), '--', label='Analytic')

	#end if
	ax.legend()
	
	#Works for Stress/Strain
	ax.set(ylim = [-0.05, 0.05], xlim=[xlowbnd,xhighbnd], xlabel='x (cm)', ylabel='Strain',title='Strain v x')
	
	#Works for Density
	#ax.set(ylim=[0.95, 1.05], xlim=[xlowbnd,xhighbnd], xlabel='x (cm)', ylabel='Density (g/cm**3)',title='Density v x')
	
	#Works for xvel
	#ax.set(ylim=[-.05, .05], xlim=[xlowbnd,xhighbnd], xlabel='x (cm)', ylabel='Velocity (cm/s)',title='Velocity v x')
	

#end animation
	
# the frames=... num can prevent the plot from crashing
ani = animation.FuncAnimation(fig, func=update, frames=tsteps//record_frequency, interval=10, save_count = tsteps//record_frequency)


Writer = animation.writers['ffmpeg']
writer = Writer(fps=15, metadata=dict(artist='Me'), bitrate=1800)

video = animation.FFMpegWriter(fps=10)

ani.save('ShakeWaveMovingStrain.mp4', writer=writer)




plt.show()
