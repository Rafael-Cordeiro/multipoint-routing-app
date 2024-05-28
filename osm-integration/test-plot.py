# Import libraries
from matplotlib import pyplot as plt
import numpy as np


# Creating dataset
cars = ['Audi', 'Bmw', 'Ford',
				'Dodge', 'Porche', 'Mercedes']

data = [23, 17, 35, 29, 12, 41]

# Creating plot

# show plot

if __name__ == '__main__':
    fig = plt.figure(figsize=(10, 7))
    plt.pie(data, labels=cars)
    plt.show()