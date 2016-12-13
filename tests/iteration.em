for(var i = 1; i < 5; ++i){
    print(i, " ");
    println("in the for loop");
}

var t = 1;
while(t < 10){
    print(t, " ");
    println("in the loop");
    ++t;
}

var s = 5;
do{
    print(s, " ");
    println("in the do-while");
    --s;
} while(s > 0);

//Iterative fibonacci
function fib(x){
    var f1 = 0;
    var f2 = 1;
    var f;
    do{
        f = f1+f2;
        f1 = f2;
        f2 = f;
        --x;
        f;
    }while(x>1);
}

print("Fib(7) = ");
println(fib(7));
