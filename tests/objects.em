function println(s){print(s, "\n");}

//Traditional Way of Defining "class"
function book(book_title){
    var title = book_title;

    function setTitle(newTitle){
        title = newTitle;
    }

    this;
}

var mybook = book("Of Mice and Men");

println(mybook.title);

//"class" syntactic sugar
class textbook(book_title){
    var title = book_title;

    function setTitle(newTitle){
        title = newTitle;
    }
}

var CS_textbook = textbook("Structure and Interpretation of Computer Programs");

println(CS_textbook.title);
