package fr.univavignon.graphcentr.g03;

import fr.univavignon.graphcentr.g07.core.graphs.SimpleGraph;
import fr.univavignon.graphcentr.g07.core.centrality.*;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;

/**
 * 
 * @author Delfeil
 * @brief compute the EigenVector centrality using the Power Method
 */
class EigenVector implements SimpleCentrality
{
	double p;
	
	public EigenVector()
	{
		this.p=0.1;
	}
	
	public EigenVector(double precision)
	{
		this.p=precision;
	}
	
	@Override
	public CentralityResult evaluate(SimpleGraph g)
	{
		CentralityResult r=new CentralityResult();
		double[] eigenV=EigneVectorAlgorithm(g);
		for(int i=0; i<eigenV.length; i++)
		{
			r.add(eigenV[i]);
		}
		return r;
	}
	
	/**
	 * 
	 * @param g the graph
	 * @return array containing each node's centrality
	 */
	public double[] EigneVectorAlgorithm(SimpleGraph g)
	{
		//Initialisation
		double[] values;
		double [][] adjMat=g.toAdjacencyMatrix();
		DenseDoubleMatrix2D adj=new DenseDoubleMatrix2D(adjMat);
		int n=g.getNodeCount();
		
			//Création de deux vecteurs aléatoires
		DoubleFactory1D randV=DoubleFactory1D.dense;
		DoubleMatrix1D v0=randV.sample(n, 1, p);	//Vecteur de l'itération précédente
		DoubleMatrix1D v1=randV.sample(n, 1, p);	//Vecteur de l'itération actuelle
		DoubleMatrix1D vtmp;
		
		//Algo
		double lambda;
		Algebra calNorm=new Algebra();
			//tant que la précision est supérieure à la différence entre la norme du vecteur de l'itération précédente, et la norme du vecteur de l'itération courante:
		while(Math.abs(calNorm.norm2(v0)-calNorm.norm2(v1))>=p)
		{
			v0.assign(v1);			//v0=v1
			v1=adj.zMult(v1, v1);	//v1= A*v1
			lambda=calNorm.norm2(v1);
			v1=v1.assign(Functions.mult(1/lambda));
		}
		values=v1.toArray();
		return values;
	}
}
