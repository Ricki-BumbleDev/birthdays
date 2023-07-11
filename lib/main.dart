import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
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
      onGenerateTitle: (context) => AppLocalizations.of(context)!.birthdays,
      theme: ThemeData(
        primarySwatch: Colors.orange,
      ),
      home: const HomePage(),
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text(AppLocalizations.of(context)!.birthdays),
        ),
        body: FutureBuilder<List<BirthdayEntry>>(
          future: getBirthdayEntries(),
          builder: (BuildContext context,
              AsyncSnapshot<List<BirthdayEntry>> snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return ListTile(
                title: Text(AppLocalizations.of(context)!.loading),
                subtitle: Text(AppLocalizations.of(context)!.loadingHint),
              );
            } else if (snapshot.hasError) {
              return ListTile(
                title: Text(AppLocalizations.of(context)!.error),
                subtitle: Text(snapshot.error.toString()),
              );
            } else {
              if (snapshot.data!.isEmpty) {
                return ListTile(
                  title: Text(
                      AppLocalizations.of(context)!.noContactsWithBirthday),
                  subtitle: Text(
                      AppLocalizations.of(context)!.noContactsWithBirthdayHint),
                );
              } else {
                return ListView.builder(
                  itemCount: snapshot.data!.length,
                  itemBuilder: (BuildContext context, int index) {
                    var birthdayEntry = snapshot.data![index];
                    return ListTile(
                      title: Text(birthdayEntry.name),
                      subtitle: Text([
                        birthdayEntry.nextAge != null
                            ? AppLocalizations.of(context)!
                                .nextAge(birthdayEntry.nextAge!)
                            : null,
                        '(${birthdayEntry.birthday})'
                      ].where((s) => s != null).join(' ')),
                      trailing: Text(AppLocalizations.of(context)!
                          .daysUntilNextBirthday(
                              birthdayEntry.daysUntilNextBirthday)),
                    );
                  },
                );
              }
            }
          },
        ));
  }
}
