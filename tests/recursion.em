function println(s){print(s, "\n");}

function fib(x){
    if(x < 2){
        x;
    }
    else{
        fib(x - 1) + fib(x - 2);
    }
}

print("Fib(35) = ");
println(fib(35));
