import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:wakelock/wakelock.dart';

import 'birthday_entries.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    if (kDebugMode) {
      Wakelock.enable();
    }
    return MaterialApp(
      title: 'Birthdays',
      theme: ThemeData(
        primarySwatch: Colors.orange,
      ),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Birthdays'),
        ),
        body: FutureBuilder<List<BirthdayEntry>>(
          future: getBirthdayEntries(),
          builder: (BuildContext context,
              AsyncSnapshot<List<BirthdayEntry>> snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const ListTile(
                title: Text('Your contact data is being loaded.'),
                subtitle: Text('It will just be a second.'),
              );
            } else if (snapshot.hasError) {
              return ListTile(
                title: const Text('Unfortunately, an error has occured.'),
                subtitle: Text(snapshot.error.toString()),
              );
            } else {
              return ListView.builder(
                itemCount: snapshot.data!.length,
                itemBuilder: (BuildContext context, int index) {
                  var birthdayEntry = snapshot.data![index];
                  return ListTile(
                    title: Text(birthdayEntry.name),
                    subtitle: Text(birthdayEntry.age == null
                        ? birthdayEntry.birthday
                        : 'turns ${birthdayEntry.age} (${birthdayEntry.birthday})'),
                    trailing: Text(birthdayEntry.daysUntil == 0
                        ? 'today'
                        : birthdayEntry.daysUntil == 1
                            ? 'tomorrow'
                            : 'in ${birthdayEntry.daysUntil} days'),
                  );
                },
              );
            }
          },
        ));
  }
}