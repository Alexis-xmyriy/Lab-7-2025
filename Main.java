import functions.*;
import functions.basic.*;

public class Main {

    public static void main(String[] args) throws  FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException, ClassNotFoundException, InterruptedException {
        ArrayTabulatedFunction arrayFunc1 = new ArrayTabulatedFunction(0, 9, 11);
        LinkedListTabulatedFunction listFunc1 = new LinkedListTabulatedFunction(4, 10, 9);

        System.out.println("\nArrayTabulatedFunction\n");
		for (FunctionPoint p : arrayFunc1) {
			System.out.println(p);
		}
        System.out.println("\nLinkedListTabulatedFunction\n");
        for (FunctionPoint p : listFunc1) {
			System.out.println(p);
		}
        System.out.println("\n-------\n");
        
        Function func2 = new Cos();
        TabulatedFunction tfunc2;
        tfunc2 = TabulatedFunctions.tabulate(func2, 0, Math.PI, 11);
        System.out.println(tfunc2.getClass());
        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tfunc2 = TabulatedFunctions.tabulate(func2, 0, Math.PI, 11);
        System.out.println(tfunc2.getClass());
        TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tfunc2 = TabulatedFunctions.tabulate(func2, 0, Math.PI, 11);
        System.out.println(tfunc2.getClass());

        System.err.println("\n-------\n");

        TabulatedFunction func3;

        func3 = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println(func3.getClass());
        System.out.println(func3);

        func3 = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println(func3.getClass());
        System.out.println(func3);

        func3 = TabulatedFunctions.createTabulatedFunction(LinkedListTabulatedFunction.class, new FunctionPoint[] {new FunctionPoint(0, 0), new FunctionPoint(10, 10)});

        System.out.println(func3.getClass());
        System.out.println(func3);

        func3 = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println(func3.getClass());
        System.out.println(func3);
    }
}