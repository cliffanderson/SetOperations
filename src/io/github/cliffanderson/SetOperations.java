package io.github.cliffanderson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SetOperations
{
	public static void main(String[] args)
	{
		new SetOperations();
	}

	public SetOperations()
	{	
		Set[] sets = loadSetsFromFile(new File("Partition_Med3.txt"), SetFormat.BIT_VECTOR);
		
		int[] elements = new int[100000];
		
		for(int i = 1; i <= elements.length; i++)
		{
			elements[i - 1] = i;
		}
		
		Set U = new Set(elements);
		int sumOfCardinalities = 0;
		for(Set s : sets)
		{
			System.out.println(s.getCardinality());
			sumOfCardinalities += s.getCardinality();
		}
		
		System.out.println("Sum of cardinalities: " + sumOfCardinalities);
		
		System.out.println(U.hasPartition(sets));
	}


	public Set[] loadSetsFromFile(File file, SetFormat format)
	{
		BufferedReader br = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			System.err.println("File not found");
			e.printStackTrace();
			return null;
		}

		List<String> lines = new ArrayList<String>();
		String line;

		try{
			while((line = br.readLine()) != null)
			{	
				lines.add(line);
			}
		}
		catch (IOException e)
		{
			System.err.println("IOExcpetion while reading from file");
			e.printStackTrace();
			return null;
		}
		
		try {
			br.close();
		} catch (IOException e) {
			System.err.println("Error while closing BufferedReader");
			e.printStackTrace();
			return null;
		}
		
		switch(format)
		{
		case BIT_VECTOR:
			if(lines.size() < 2)
			{
				System.err.println("Error: File does not contain enough information for bit vector format");
			}
			
			int n = Integer.parseInt(lines.get(0));
			
			//get number of sets
			String[] firstLine = lines.get(1).split(" ");
			
			int numberOfSets = firstLine.length;
			
			if(numberOfSets < 1)
			{
				System.err.println("Invalid data for bit vector format: Can not have 0 sets");
				return null;
			}
			
			int[][] data = new int[numberOfSets][n];
			
			for(int i = 1; i < lines.size(); i++)
			{
				String[] elements = lines.get(i).split(" ");
				
				for(int j = 0; j < elements.length; j++)
				{
					data[j][i - 1] = elements[j].equals("0") ? -1 : i;
				}
			}
			
			Set[] sets = new Set[numberOfSets];
			
			for(int x = 0; x < sets.length; x++)
			{
				sets[x] = new Set(data[x]);
			}
			
			//now go through each set and take out the negatives ones 
			for(int x = 0; x < sets.length; x++)
			{
				Set set = sets[x];
				int[] elements = set.getElements();
				
				List<Integer> newElementsList = new ArrayList<Integer>();
				
				for(int i : elements)
				{
					if(i != -1)
					{
						newElementsList.add(i);
					}
				}
				
				int[] newElements = new int[newElementsList.size()];
				
				for(int i = 0; i < newElements.length; i++)
				{
					newElements[i] = newElementsList.get(i);
				}
				
				set.setElements(newElements);				
			}
			
			return sets;
			
			
		default: 
			return null;
		}
	}
}

//class describing a set
//contains the elements and operations to be done with other sets
class Set
{
	private int[] elements;

	public Set(int...elements)
	{
		this.elements = elements;
	}

	public int getCardinality()
	{
		return elements.length;
	}

	public boolean contains(int i)
	{
		for(int num : elements)
		{
			if(num == i)
			{
				return true;
			}
		}

		return false;
	}
	
	public int[] getElements()
	{
		return elements;
	}
	
	public void setElements(int[] elements)
	{
		this.elements = elements;
	}

	public boolean isSubsetOf(Set s)
	{
		for(int i : elements)
		{
			//for each element in this set, check if it is
			//in the passed set
			if(!s.contains(i))
			{
				return false;
			}
		}

		return false;
	}
	
	public boolean hasPartition(Set[] sets)
	{
		for(int i : elements)
		{
			//number of times i appears in all sets passed to the method
			int amount = 0;
			for(Set set : sets)
			{
				if(set.contains(i))
					amount++;
			}
			
			if(amount == 0)
			{
				System.out.println("No partition. " + i + " did not appear any subsets");
			}
			else if(amount > 1)
			{
				System.out.println("No partition. " + i + " appeared more than once in the subsets");
			}
		}
		return true;
	}
}

enum SetFormat
{
	BIT_VECTOR
	//first line contains n, the number of lines following it
	//all lines after the first take the format of 0/1 0/1 0/1
	//where a 1 indicates the element is contained it the set, and a 0 does not
	//Example:
	
	//2
	//0 0 1 0
	//1 1 1 0
	
	//In this case A = {1}, B = {1}, C = {1, 2} and D = the empty set
	;
}