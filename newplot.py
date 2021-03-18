import math
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

from plot_constants import *


mesh_data = pd.read_csv(r'/home/kyleperez/javastuff/admpm/test.csv', header=0)

"""
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







# Length of dataset imported as num_nodes

xpart = np.linspace(0, 1, num_nodes)


x1 = mesh_data['xpos'][0 : num_nodes]
x2 = mesh_data['xpos'][num_nodes : 2*num_nodes]
x3 = mesh_data['xpos'][2*num_nodes : 3*num_nodes]

y1 = mesh_data['xvel'][0 : num_nodes]
y2 = mesh_data['xvel'][num_nodes : 2*num_nodes]
y3 = mesh_data['xvel'][2*num_nodes : 3*num_nodes]

y4 = mesh_data['strain'][0 : num_nodes]
y5 = mesh_data['strain'][num_nodes : 2*num_nodes]
y6 = mesh_data['strain'][2*num_nodes : 3*num_nodes]

y7 = mesh_data['dens'][0 : num_nodes]
y8 = mesh_data['dens'][num_nodes : 2*num_nodes]
y9 = mesh_data['dens'][2*num_nodes : 3*num_nodes]


plt.figure(0)
plt.plot( x1, y1, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Xvel', xpart, 0), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('X Velocity (cm/s)')
plt.title('X Velocity v X at t = 0')
plt.legend()


plt.figure(1)
plt.plot( x2, y2, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Xvel', xpart, dt), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('X Velocity (cm/s)')
plt.title('X Velocity v X at t = 0.001')
plt.legend()


plt.figure(2)
plt.plot( x3, y3, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Xvel', xpart, 2*dt), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('X Velocity (cm/s)')
plt.title('X Velocity v X at t = 0.002')
plt.legend()



plt.figure(3)
plt.plot( x1, y4, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Strain', xpart, 0), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('Strain')
plt.title('Strain v X at t = 0')
plt.legend()


plt.figure(4)
plt.plot( x2, y5, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Strain', xpart, dt), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('Strain')
plt.title('Strain v X at t = 0.001')
plt.legend()


plt.figure(5)
plt.plot( x3, y6, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Strain', xpart, 2*dt), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('Strain')
plt.title('Strain v X at t = 0.002')
plt.legend()



plt.figure(6)
plt.plot( x1, y7, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Density', xpart, 0), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('Density')
plt.title('Density v X at t = 0')
plt.legend()

plt.figure(7)
plt.plot( x2, y8, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Density', xpart, dt), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('Density')
plt.title('Density v X at t = 0.001')
plt.legend()

plt.figure(8)
plt.plot( x3, y9, '-', label='Computed' )
plt.plot( xpart, analytic_solution('Density', xpart, 2*dt), '--', label='Analytic')
plt.xlabel('x (cm)')
plt.ylabel('Density')
plt.title('Density v X at t = 0.002')
plt.legend()



plt.show()

