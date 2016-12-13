function fib(x){
    if(x < 2){
        x;
    }
    else{
        fib(x - 1) + fib(x - 2);
    }
}

print("Fib(30) = ");
println(fib(30));
