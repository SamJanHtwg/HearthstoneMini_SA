package hearthstoneMini
package aview

/*
Ansi Codes: https://gist.github.com/dominikwilkowski/60eed2ea722183769d586c76f22098dd
*/

object Strings {
  val zeilenUmbruch: String = "\n"
  val endGameMsg: String = "Schönes Spiel!"
  val chooseGameMode: String = "Bitte Spielmodus auswählen: " +
    "\n[1] Normal: Start mit: 30 Healthpoints & 1 Mana" +
    "\n[2] Hardcore: Start mit: 10 Healthpoints & 5 Mana " +
    "\n[3] Admin: Start mit: 100 Healthpints & 100 Mana"
  val enterPlayerNames: String = "Bitte Spielernamen 1 & 2 eingeben: "
  val istDranMsg: String = " ist dran!"
  val gewonnenMsg: String = " hat gewonnen!!"
  val commands: String = "place(hand,solt) | d-draw() | a-attack(yours, theirs) |" +
    " e-direct attack | " + "s-Endturn |" + "\n" + "z-undo | y-redo | q-Quit"
  val cleanScreen: String = "\u001b[2J"
  val colorYellow: String = "\u001b[33m"
  val boldText: String = "\u001b[1m"
  val resetStyles: String = "\u001b[0m"
}
