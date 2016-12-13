class Stack(){
    class LinkedListNode(value){
        var next;
        var value = value;

        function getValue(){ value; }
        function setValue(v){ value = v;}

        function getNext(){next;}
        function setNext(n){next = n;}
    }

    var size = 0;
    var head = null;

    function push(x){
        var newnode = LinkedListNode(x);
        ++size;
        newnode.setNext(head);
        head = newnode;
        x;
    }

    function pop(){
        var return = head;
        if(head != null){
            head = head.getNext();
            --size;
            return.getValue();
        }
        else{
            println("ERROR: Attempted to pop an empty stack");
            false;
        }
    }
}

var s = Stack();

function power(base, exponent){
    if(exponent == 0){
        1;
    }
    else{
        while(exponent > 0){
            if(exponent == 1){
                --exponent;
                base;
            }
            else{
                base = base * base;
                --exponent;
            }
        }
    }
}


while(scannerHasNext()){
    var token = scanToken();
    if(type(token) == "INTEGER"){
        //New number on the stack
        s.push(token);
    }
    else{
        //It's an operator
        var v1 = s.pop();
        var v2 = s.pop();
        if(token == "+"){
        
            s.push((v2 + v1));
        }
        else if(token == "-"){
        
            s.push((v2 - v1));
        }
        else if(token == "*"){
        
            s.push((v2 * v1));
        }
        else if(token == "/"){
        
            s.push((v2 / v1));
        }
        else if(token == "^"){
        
            s.push(power(v1, v2));
        }
    }
}

println(s.pop());

