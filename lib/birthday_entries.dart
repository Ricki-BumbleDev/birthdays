import 'package:flutter_contacts/flutter_contacts.dart';

class BirthdayEntry {
  String name;
  String birthday;
  int daysUntilNextBirthday;
  int? nextAge;

  BirthdayEntry(
      {required this.name,
      required this.birthday,
      required this.daysUntilNextBirthday,
      this.nextAge});
}

Future<List<Contact>> getContacts() async {
  var isPermissionGranted =
      await FlutterContacts.requestPermission(readonly: true);
  if (isPermissionGranted) {
    var contacts = await FlutterContacts.getContacts(
        withProperties: true, withPhoto: false);
    return contacts;
  } else {
    throw Exception('Permission not granted');
  }
}

BirthdayEntry getBirthdayEntry(Contact contact) {
  var event =
      contact.events.firstWhere((event) => event.label == EventLabel.birthday);
  var nowRaw = DateTime.now();
  var now = DateTime(nowRaw.year, nowRaw.month, nowRaw.day);
  var birthdayThisYear = DateTime(now.year, event.month, event.day);
  var nextBirthday = birthdayThisYear.isAfter(now)
      ? birthdayThisYear
      : DateTime(birthdayThisYear.year + 1, birthdayThisYear.month,
          birthdayThisYear.day);
  var daysUntilNextBirthday = nextBirthday.difference(now).inDays;
  if (event.year == null) {
    return BirthdayEntry(
        name: contact.displayName,
        birthday:
            '${event.month.toString().padLeft(2, '0')}-${event.day.toString().padLeft(2, '0')}',
        daysUntilNextBirthday: daysUntilNextBirthday);
  } else {
    var trueBirthday = DateTime(event.year!, event.month, event.day);
    var nextAge = nextBirthday.year - trueBirthday.year;
    return BirthdayEntry(
        name: contact.displayName,
        birthday:
            '${event.year}-${event.month.toString().padLeft(2, '0')}-${event.day.toString().padLeft(2, '0')}',
        daysUntilNextBirthday: daysUntilNextBirthday,
        nextAge: nextAge);
  }
}

Future<List<BirthdayEntry>> getBirthdayEntries() async {
  var contacts = await getContacts();
  var birthdayEntries = contacts
      .where((contact) =>
          contact.events.any((event) => event.label == EventLabel.birthday))
      .map(getBirthdayEntry)
      .toList();
  birthdayEntries.sort(
      (a, b) => a.daysUntilNextBirthday.compareTo(b.daysUntilNextBirthday));
  return birthdayEntries;
}
