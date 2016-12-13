class Node(){
    var value;
    var key;
    var right;
    var left;
    var parent;

    function getLeft(){left;}
    function getRight(){right;}
    function getParent(){parent;}
    function getValue(){value;}
    function getKey(){key;}

    function setLeft(n){left = n;}
    function setRight(n){right = n;}
    function setParent(n){parent = n;}
    function setValue(v){value = v;}
    function setKey(k){key = k;}

    function isLeftChild(){
        (self.getParent().getLeft() is self);
    }

    function isRightChild(){
        (self.getParent().getRight() is self);
    }

    function getBalance(){
        if(self.getRight() != null && self.getLeft() == null){
            self.getRight().getHeight();
        }
        else if(self.getLeft() != null && self.getRight() == null){
            0 - self.getLeft().getHeight();
        }
        else if(self.getLeft() != null && self.getRight() != null){
            self.getRight().getHeight() - self.getLeft().getHeight();
        }
        else{
            0;
        }
    }

    function getSibling(){
        if(self.isRightChild()){
            self.getParent().getLeft();
        }
        else{
            self.getParent().getRight();
        }
    }

    function isFavoredChild(){
        var p = self.getParent();
        if(p != null){
            var bal = p.getBalance();
            if(bal == 0){
                false;
            }
            else if(bal < 0 && self.isLeftChild()){
                true;
            }
            else if(bal > 0 && self.isRightChild()){
                true;
            }
            else{
                false;
            }    
        }
        else{
            false;
        }
        
    }

    function getFavoredChild(){
        var bal = self.getBalance();
        if(bal < 0){
            self.getLeft();
        }
        else if(bal > 0){
            self.getRight();
        }
        else{
            (null);
        }
    }

    function getHeight(){
        var lheight;
        if(self.getLeft() != null){
            lheight = 1 + self.getLeft().getHeight();
        }
        else{
            lheight = 1;
        }
        var rheight;
        if(self.getRight() != null){
            rheight = 1 + self.getRight().getHeight();
        }
        else{
            rheight = 1;
        }
        if(lheight < rheight){
            rheight;
        }
        else{
            lheight;
        }
    }

    function getRightHeight(){
        if(self.getRight() != null){
            self.getRight().getHeight();
        }
        else{
            0;
        }
    }

    function getLeftHeight(){
        if(self.getLeft() != null){
            self.getLeft().getHeight();
        }
        else{
            0;
        }
    }
}

class AVLTree(){
    var root;
    var treesize = 0;

    function binaryTreeInsert(n, root, parent, isLeft){
        if(root == null){
            n.setParent(parent);
            if(isLeft != null){
                if(isLeft){
                    println(n);
                    parent.setLeft(n);
                }
                else{
                    parent.setRight(n);
                }
            }
        }
        else{
            if(n.getValue() <= root.getValue()){
                binaryTreeInsert(n, root.getLeft(), root, true);
            }
            else{
                binaryTreeInsert(n, root.getRight(), root, false);  
            }
        }
    }

    function rotateToParent(x){
        var p = x.getParent();
        var gp = p.getParent();
        var y = x.getLeft();
        var z = x.getRight();

        if(gp != null){
            if(p.isLeftChild()){
                gp.setLeft(x);
            }
            else{
                gp.setRight(x);
            }
        }
        else{
            if(root is p){
                root = x;
            }
        }

        x.setParent(gp);
        p.setParent(x);
        if(p.getLeft() is x){
            p.setLeft(z);
            x.setRight(p);
            if(z != null){
                z.setParent(p);
            }
        }
        else{
            p.setRight(y);
            x.setLeft(p);
            if(y != null){
                y.setParent(p);
            }
        }
    }

    function insertionFixUp(x){
        var p = x.getParent();
        if(p == null){
            root = x;
        }
        else if(!x.isFavoredChild()){
            x;
        }
        else if(p.getBalance() == 0){
            insertionFixUp(p);
        }
        else{
            var gp = p.getParent();
            var y = x.getFavoredChild();
            if(y != null && !((x.isLeftChild() && y.isLeftChild()) || 
                                (x.isRightChild() && y.isRightChild()))){
                print("Double Rotation\n");
                rotateToParent(y);
                rotateToParent(y);
            }
            else{
                print("Single Rotation\n");
                rotateToParent(x);
            }
        }
    }

    function insert(newnode){
        if(root == null){
            root = newnode;
        }
        else{
            binaryTreeInsert(newnode, root, null, null);
        }
        ++treesize;
        insertionFixUp(newnode);
    }

    function find(key){
        function findHelper(root, x){
            if(root == null){
                false;
            }
            else if(root.getKey() == x){
                root;
            }
            else if(x < root.getKey()){
                findHelper(root.getLeft(), x);
            }
            else {
                findHelper(root.getRight(), x);
            }
        }
        findHelper(root, key);
    }

    function size(){
        treesize;
    }

    function printGivenLevel(root, level){
        if(root == null){
            false;
        }
        else if(level == 1){
            var specialHeight = root.getLeftHeight() - root.getRightHeight();
            print(root.getKey(), ":", specialHeight, " ");
        }
        else{ // level > 1
            printGivenLevel(root.getLeft(), level - 1);
            printGivenLevel(root.getRight(), level - 1);
        }
    }

    function printStatistics(){
        function printLevelOrder(height, maxHeight){
            printGivenLevel(root, height);
            if(height < maxHeight){
                printLevelOrder(height + 1, maxHeight);
            }
        }
        printLevelOrder(1, root.getHeight());
        print("\n");
    }
}

class Dictionary(){
    var tree = AVLTree();

    function insert(key, value){
        var newnode = Node();
        newnode.setKey(key);
        newnode.setValue(value);
        tree.insert(newnode);
    }
    function get(key){
        tree.find(key).getValue();
    }

    function stat(){
        tree.printStatistics();
    }

}

var d = Dictionary();
d.insert("a", 1);
d.insert("b", 2);
d.insert("c", 3);
d.insert("d", 4);

print("d.get(c) = ", d.get("c"), "\n");
print("d.get(a) = ", d.get("a"), "\n");
print("d.get(d) = ", d.get("d"), "\n");
print("d.get(b) = ", d.get("b"), "\n");
