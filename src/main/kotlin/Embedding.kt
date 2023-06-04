class Image(link: String) : Embedding(link) {
}

class KalturaVideo(link: String) : Embedding(link) {
}


abstract class Embedding(
    val link: String
) {

}