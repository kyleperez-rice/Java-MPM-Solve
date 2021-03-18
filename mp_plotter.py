import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import pandas as pd

from plot_constants import *


mp_data = pd.read_csv(r'/home/kyleperez/javastuff/admpm/mp_test.csv', header=0)



fig = plt.figure()
ax = fig.add_subplot(1,1,1)


# (Number of nodes - 1) * num_mps per cell
length = (num_nodes - 1) * num_mps

x = np.linspace(0, length, length)
lb = np.zeros(length)
ub = 1 + lb

def animation(i):



	x = mp_data['xpos'][length*i : length*(i+1)]

	y = mp_data['strain'][length*i : length*(i+1)]

	time = mp_data['time'][length*i]

	ax.clear()
	ax.plot(x, y, '-', label='Simulation')

	ax.legend()

	ax.set(ylim = [-0.015, 0.015], xlim=[xlowbnd,xhighbnd])



def pos_animation(i):



	y = mp_data['xpos'][length*i : length*(i+1)]

	time = mp_data['time'][length*i]

	ax.clear()
	ax.plot(x, y, '.', label='Mp position')
	ax.plot(x, lb, '--', label='Boundary 0')
	ax.plot(x, ub, '--', label='Boundary 1')

	ax.set(ylim=[-0.1, 1.1], xlim = [0, length])



#animation = FuncAnimation(fig, func=animation, frames=tsteps, interval=10)

#x = mp_data['xpos'][0, length]
#z = mp_data['xpos'][0 : length]
#y = mp_data['mass'][0 : length]

plt.figure(1)

x0 = mp_data['xpos'][0 : length]
y0 = mp_data['mass'][0 : length]

plt.plot(x0, y0, '.', label='Mass')
plt.title('Strain v xpos at t = 0')



plt.figure(2)

x1 = mp_data['xpos'][length : 2*length]
y1 = mp_data['mass'][length : 2*length]

plt.plot(x1, y1, '.', label='Mass')
plt.title('Strain v xpos at t = 0.001')




plt.figure(3)

x2 = mp_data['xpos'][2*length : 3*length]
y2 = mp_data['mass'][2*length : 3*length]

plt.plot(x2, y2, '.', label='Mass')
plt.title('Strain v xpos at t = 0.002')

plt.show()
