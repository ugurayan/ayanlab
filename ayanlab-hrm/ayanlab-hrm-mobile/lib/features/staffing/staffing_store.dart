import 'package:flutter/material.dart';

import 'staffing_theme.dart';

const kJobRoute = '/job';
const kCompanyName = 'Ayan Kitchen';

DateTime dateOnly(DateTime value) => DateTime(value.year, value.month, value.day);

bool sameDay(DateTime a, DateTime b) =>
    a.year == b.year && a.month == b.month && a.day == b.day;

DateTime startOfWeek(DateTime value) {
  final day = dateOnly(value);
  return day.subtract(Duration(days: day.weekday % 7));
}

class Worksite {
  const Worksite({required this.id, required this.name, required this.address});

  final String id;
  final String name;
  final String address;
}

class SiteContact {
  SiteContact({required this.id, required this.name, required this.email});

  final String id;
  final String name;
  final String email;
}

class JobProfile {
  const JobProfile({
    required this.name,
    required this.summary,
    required this.wage,
    this.skills = const [],
    this.expertise = const [],
  });

  final String name;
  final String summary;
  final double wage;
  final List<String> skills;
  final List<String> expertise;
}

class DressOption {
  const DressOption({
    required this.id,
    required this.title,
    required this.detail,
    required this.icon,
  });

  final String id;
  final String title;
  final String detail;
  final IconData icon;
}

class ClothingItem {
  const ClothingItem({
    required this.name,
    required this.detail,
    required this.icon,
  });

  final String name;
  final String detail;
  final IconData icon;
}

class ShiftSlot {
  ShiftSlot({
    required this.id,
    required this.date,
    required this.startMin,
    required this.endMin,
    required this.breakMin,
    required this.workers,
    this.po,
  });

  final String id;
  DateTime date;
  int startMin;
  int endMin;
  int breakMin;
  int workers;
  String? po;

  int get workMinutes {
    final span = endMin - startMin - breakMin;
    return span < 0 ? 0 : span;
  }

  ShiftSlot copy({DateTime? date}) => ShiftSlot(
        id: id,
        date: date ?? this.date,
        startMin: startMin,
        endMin: endMin,
        breakMin: breakMin,
        workers: workers,
        po: po,
      );
}

class Applicant {
  Applicant({
    required this.id,
    required this.name,
    required this.role,
    required this.rating,
    required this.reviews,
    required this.wageLabel,
    required this.favouriteOf,
    required this.travel,
    required this.email,
    required this.phone,
    required this.area,
    required this.about,
    required this.softSkills,
    required this.skills,
    required this.languages,
    required this.history,
    required this.documents,
    this.status = 'applied',
    this.saved = false,
  });

  final String id;
  final String name;
  final String role;
  final double rating;
  final int reviews;
  final String wageLabel;
  final int favouriteOf;
  final String travel;
  final String email;
  final String phone;
  final String area;
  final String about;
  final List<(String, int)> softSkills;
  final List<(String, int)> skills;
  final List<(String, String, String)> languages;
  final List<(String, String, String)> history;
  final List<String> documents;
  String status;
  bool saved;
}

class StaffJob {
  StaffJob({
    required this.id,
    required this.publicId,
    required this.name,
    required this.roles,
    required this.summary,
    required this.safety,
    required this.wage,
    required this.locationName,
    required this.locationAddress,
    required this.dressId,
    required this.clothingNote,
    required this.shifts,
    required this.applicants,
    required this.autoHire,
    required this.pool,
    required this.contactName,
    required this.contactEmail,
    this.status = 'open',
  });

  final String id;
  final String publicId;
  String name;
  List<String> roles;
  String summary;
  String safety;
  double wage;
  String locationName;
  String locationAddress;
  String dressId;
  String clothingNote;
  List<ShiftSlot> shifts;
  List<Applicant> applicants;
  bool autoHire;
  String pool;
  String contactName;
  String contactEmail;
  String status;

  double get cost => (wage * 1.43 * 100).roundToDouble() / 100;

  int get needed => shifts.fold(0, (sum, shift) => sum + shift.workers);

  int get hired => applicants.where((a) => a.status == 'hired').length;

  int get appliedCount => applicants.where((a) => a.status == 'applied').length;

  bool get filled => needed > 0 && hired >= needed;

  bool get open => status == 'open';

  ShiftSlot? get firstShift {
    if (shifts.isEmpty) return null;
    final copy = [...shifts]..sort((a, b) => a.date.compareTo(b.date));
    return copy.first;
  }
}

class HourEntry {
  HourEntry({
    required this.id,
    required this.worker,
    required this.role,
    required this.date,
    required this.time,
    required this.hours,
    this.approved = false,
  });

  final String id;
  final String worker;
  final String role;
  final DateTime date;
  final String time;
  final double hours;
  bool approved;
}

class JobDraft {
  String name = '';
  String summary = '';
  String safety = '';
  List<String> roles = [];
  double wage = 32;
  String locationId = '';
  String locationNotes = '';
  String dressId = '';
  String grooming = 'Professional';
  String clothingNote = '';
  Set<String> clothing = {};
  Set<String> skills = {};
  Set<String> expertise = {};
  Set<String> certificates = {};
  List<ShiftSlot> shifts = [];
  String contactId = '';
  bool autoHire = true;
  String pool = 'trusted';
  bool pdfAttached = false;

  double get cost => (wage * 1.43 * 100).roundToDouble() / 100;

  double get estimatedTotal {
    final hours = shifts.fold<double>(
      0,
      (sum, shift) => sum + shift.workers * shift.workMinutes / 60,
    );
    return hours * cost;
  }
}

const worksites = <Worksite>[
  Worksite(
    id: 'loc-limmat',
    name: 'Ayan Kitchen',
    address: 'Limmatquai 80, 8001 Zurich',
  ),
  Worksite(
    id: 'loc-bahnhof',
    name: 'Ayan Brasserie',
    address: 'Bahnhofstrasse 14, 8001 Zurich',
  ),
  Worksite(
    id: 'loc-sihl',
    name: 'Sihl Events',
    address: 'Sihlstrasse 22, 8001 Zurich',
  ),
];

const jobProfiles = <JobProfile>[
  JobProfile(
    name: 'Barback',
    summary:
        'Keeps the bar stocked, glassware clean, and the team moving during service.',
    wage: 32,
    skills: ['Bar setup', 'Bar breakdown', 'Cleaning', 'High volume'],
    expertise: ['Beer', 'Spirits'],
  ),
  JobProfile(
    name: 'Barista',
    summary: 'Prepares coffee drinks to recipe and keeps the bar tidy between orders.',
    wage: 34,
    skills: ['Drinks service', 'Cash handling', 'Cleaning'],
    expertise: ['High volume'],
  ),
  JobProfile(
    name: 'Bartender',
    summary:
        'Serves drinks, builds cocktails, and looks after guests at the bar.',
    wage: 36,
    skills: ['Cocktails', 'Cash handling', 'Drinks service', 'Bar setup'],
    expertise: ['Cocktails', 'Spirits', 'Wine'],
  ),
  JobProfile(
    name: 'Waiter / Waitress',
    summary: 'Takes orders, runs food, and closes tables through a full service.',
    wage: 33,
    skills: ['Food service', 'Drinks service', 'Cash handling', 'High volume'],
    expertise: ['Wine', 'Fine dining'],
  ),
  JobProfile(
    name: 'Floor host',
    summary: 'Seats guests, manages the waitlist, and keeps the floor paced.',
    wage: 33,
    skills: ['Guest welcome', 'Reservations'],
    expertise: ['Fine dining'],
  ),
  JobProfile(
    name: 'Commis chef',
    summary: 'Preps ingredients and supports the line during service.',
    wage: 34,
    skills: ['Food preparation', 'Cleaning', 'High volume'],
    expertise: ['Hot kitchen'],
  ),
  JobProfile(
    name: 'Chef de partie',
    summary: 'Runs one section of the line and plates to the pass.',
    wage: 42,
    skills: ['Food preparation', 'Section lead', 'High volume'],
    expertise: ['Hot kitchen'],
  ),
  JobProfile(
    name: 'Kitchen porter',
    summary: 'Washes, resets pots, and keeps the dish area clear.',
    wage: 30,
    skills: ['Cleaning', 'High volume'],
    expertise: [],
  ),
  JobProfile(
    name: 'Dishwasher',
    summary: 'Runs the dish machine and returns cleanware to the line.',
    wage: 30,
    skills: ['Cleaning', 'High volume'],
    expertise: [],
  ),
  JobProfile(
    name: 'Housekeeper',
    summary: 'Turns rooms and public areas to the property standard.',
    wage: 31,
    skills: ['Cleaning', 'Room reset'],
    expertise: [],
  ),
  JobProfile(
    name: 'Receptionist',
    summary: 'Checks guests in, answers the desk, and handles simple billing.',
    wage: 35,
    skills: ['Guest welcome', 'Cash handling', 'Reservations'],
    expertise: [],
  ),
  JobProfile(
    name: 'Banquet server',
    summary: 'Serves seated events and clears on the banquet captain’s call.',
    wage: 34,
    skills: ['Banquet service', 'Drinks service', 'High volume'],
    expertise: ['Wine'],
  ),
  JobProfile(
    name: 'Runner',
    summary: 'Carries plates from the pass and resets tables between courses.',
    wage: 31,
    skills: ['Food service', 'High volume', 'Cleaning'],
    expertise: [],
  ),
  JobProfile(
    name: 'Pastry assistant',
    summary: 'Portions dessert and keeps the pastry station stocked.',
    wage: 33,
    skills: ['Food preparation', 'Cleaning'],
    expertise: ['Pastry'],
  ),
  JobProfile(
    name: 'Event setup',
    summary: 'Builds the room, sets tables, and strikes the event afterwards.',
    wage: 32,
    skills: ['Room setup', 'High volume'],
    expertise: [],
  ),
  JobProfile(
    name: 'Sommelier',
    summary: 'Recommends wine, serves it correctly, and tracks the cellar list.',
    wage: 44,
    skills: ['Wine service', 'Guest welcome'],
    expertise: ['Wine'],
  ),
];

const dressOptions = <DressOption>[
  DressOption(
    id: 'casual',
    title: 'Casual clothes',
    detail:
        'Clean clothes with no stains, holes, or sportswear. Closed shoes.',
    icon: Icons.checkroom_outlined,
  ),
  DressOption(
    id: 'smart-casual',
    title: 'Smart casual',
    detail:
        'Dark trousers or a knee-length skirt, a tidy shirt, and clean closed shoes.',
    icon: Icons.dry_cleaning_outlined,
  ),
  DressOption(
    id: 'office',
    title: 'Smart office wear',
    detail: 'Suit, shirt, and leather or leather-type shoes. Tie when asked.',
    icon: Icons.business_center_outlined,
  ),
  DressOption(
    id: 'provided',
    title: 'Uniform provided',
    detail: 'The workplace hands out the uniform on arrival.',
    icon: Icons.inventory_2_outlined,
  ),
  DressOption(
    id: 'custom',
    title: 'Create uniform',
    detail: 'Pick the exact clothing and tools to bring.',
    icon: Icons.add_circle_outline,
  ),
];

const clothingItems = <ClothingItem>[
  ClothingItem(
    name: 'White long-sleeved shirt',
    detail: 'Classic white shirt, stiff collar, ironed.',
    icon: Icons.checkroom_outlined,
  ),
  ClothingItem(
    name: 'Black long-sleeved shirt',
    detail: 'Classic black shirt, stiff collar, ironed.',
    icon: Icons.checkroom,
  ),
  ClothingItem(
    name: 'Black tailored trousers',
    detail: 'Hem sits just above the heel.',
    icon: Icons.straighten,
  ),
  ClothingItem(
    name: 'Black classic shoes',
    detail: 'Polished black closed shoes.',
    icon: Icons.ice_skating_outlined,
  ),
  ClothingItem(
    name: 'Black apron',
    detail: 'Full-length black apron, clean and unbranded.',
    icon: Icons.restaurant,
  ),
  ClothingItem(
    name: 'White t-shirt',
    detail: 'Plain white t-shirt, crease free.',
    icon: Icons.dry_cleaning_outlined,
  ),
  ClothingItem(
    name: 'Black t-shirt',
    detail: 'Plain black t-shirt, crease free.',
    icon: Icons.dry_cleaning,
  ),
  ClothingItem(
    name: 'Chef jacket',
    detail: 'Clean chef jacket in white or black.',
    icon: Icons.soup_kitchen_outlined,
  ),
  ClothingItem(
    name: 'Non-slip shoes',
    detail: 'Closed kitchen shoes with a non-slip sole.',
    icon: Icons.hiking_outlined,
  ),
  ClothingItem(
    name: 'Waiter’s friend',
    detail: 'Corkscrew and bottle opener.',
    icon: Icons.wine_bar_outlined,
  ),
  ClothingItem(
    name: 'Black bow tie',
    detail: 'Stain-free black bow tie.',
    icon: Icons.workspace_premium_outlined,
  ),
  ClothingItem(
    name: 'Hair restraint',
    detail: 'Hair tied back or covered for the kitchen.',
    icon: Icons.face_retouching_natural_outlined,
  ),
];

const expertiseOptions = [
  'Beer',
  'Champagne',
  'Cocktails',
  'Fine dining',
  'Hot kitchen',
  'Pastry',
  'Spirits',
  'Wine',
];

const skillOptions = [
  'Banquet service',
  'Bar breakdown',
  'Bar setup',
  'Cash handling',
  'Cleaning',
  'Drinks service',
  'Food preparation',
  'Food service',
  'Guest welcome',
  'High volume',
  'Reservations',
  'Room setup',
  'Wine service',
];

const certificateOptions = [
  'Food hygiene',
  'Allergen awareness',
  'Alcohol service',
  'First aid',
  'Fire marshal',
];

const groomingOptions = ['Elevated', 'Professional', 'Standard', 'No preference'];

const stopReasons = [
  'Internal staff found',
  'External staff found',
  'Job postponed',
  'Job cancelled',
  'Need fewer workers',
  'Insufficient applicants',
  'Lack of qualified applicants',
  'Bad weather',
  'Mistake during posting',
  'Posting was a test',
];

class StaffingStore extends ChangeNotifier {
  StaffingStore() {
    _seed();
  }

  final jobs = <StaffJob>[];
  final hours = <HourEntry>[];
  final contacts = <SiteContact>[];
  final draft = JobDraft();
  DateTime selectedDay = dateOnly(DateTime.now());
  String? editingJobId;

  int _seq = 1;
  String _id(String prefix) => '$prefix-${_seq++}';

  void touch() => notifyListeners();

  StaffJob? jobById(String id) {
    for (final job in jobs) {
      if (job.id == id) return job;
    }
    return null;
  }

  Worksite? worksiteById(String id) {
    for (final site in worksites) {
      if (site.id == id) return site;
    }
    return null;
  }

  SiteContact? contactById(String id) {
    for (final contact in contacts) {
      if (contact.id == id) return contact;
    }
    return null;
  }

  DressOption dressById(String id) {
    for (final option in dressOptions) {
      if (option.id == id) return option;
    }
    return dressOptions.first;
  }

  void selectDay(DateTime day) {
    selectedDay = dateOnly(day);
    notifyListeners();
  }

  void startBlank({bool notify = true}) {
    editingJobId = null;
    draft
      ..name = ''
      ..summary = ''
      ..safety = ''
      ..roles = []
      ..wage = 32
      ..locationId = worksites.first.id
      ..locationNotes = ''
      ..dressId = ''
      ..grooming = 'Professional'
      ..clothingNote = ''
      ..clothing = {}
      ..skills = {}
      ..expertise = {}
      ..certificates = {}
      ..shifts = []
      ..contactId = contacts.first.id
      ..autoHire = true
      ..pool = 'trusted'
      ..pdfAttached = false;
    if (notify) notifyListeners();
  }

  void applyProfile(JobProfile profile, {bool append = false}) {
    if (append) {
      if (!draft.roles.contains(profile.name)) draft.roles.add(profile.name);
    } else {
      draft
        ..roles = [profile.name]
        ..name = draft.name.isEmpty ? profile.name : draft.name
        ..summary = profile.summary
        ..wage = profile.wage
        ..safety = draft.safety.isEmpty
            ? 'Wet floors, hot equipment, and carrying trays or crates.'
            : draft.safety
        ..skills = {...profile.skills}
        ..expertise = {...profile.expertise};
    }
    notifyListeners();
  }

  void applyTemplate(String role) {
    final profile = jobProfiles.firstWhere((item) => item.name == role);
    startBlank();
    applyProfile(profile);
    draft
      ..name = profile.name
      ..dressId = role.contains('Chef') || role == 'Kitchen porter' || role == 'Dishwasher'
          ? 'provided'
          : 'smart-casual';
    final tomorrow = dateOnly(DateTime.now()).add(const Duration(days: 1));
    draft.shifts = [
      ShiftSlot(
        id: _id('shift'),
        date: tomorrow,
        startMin: 9 * 60,
        endMin: 17 * 60,
        breakMin: 60,
        workers: 2,
      ),
    ];
    notifyListeners();
  }

  void cloneJob(StaffJob job) {
    editingJobId = null;
    _copyJobIntoDraft(job);
    draft.name = job.name;
    final today = dateOnly(DateTime.now());
    draft.shifts = [
      for (var i = 0; i < job.shifts.length; i++)
        ShiftSlot(
          id: _id('shift'),
          date: today.add(Duration(days: i + 1)),
          startMin: job.shifts[i].startMin,
          endMin: job.shifts[i].endMin,
          breakMin: job.shifts[i].breakMin,
          workers: job.shifts[i].workers,
        ),
    ];
    notifyListeners();
  }

  void beginEdit(String jobId) {
    final job = jobById(jobId);
    if (job == null) return;
    editingJobId = jobId;
    _copyJobIntoDraft(job);
    notifyListeners();
  }

  void _copyJobIntoDraft(StaffJob job) {
    draft
      ..name = job.name
      ..summary = job.summary
      ..safety = job.safety
      ..roles = [...job.roles]
      ..wage = job.wage
      ..locationId = worksites
          .firstWhere(
            (site) => site.name == job.locationName,
            orElse: () => worksites.first,
          )
          .id
      ..dressId = job.dressId
      ..clothingNote = job.clothingNote
      ..contactId = contacts
          .firstWhere(
            (contact) => contact.name == job.contactName,
            orElse: () => contacts.first,
          )
          .id
      ..autoHire = job.autoHire
      ..pool = job.pool
      ..shifts = [
        for (final shift in job.shifts)
          ShiftSlot(
            id: shift.id,
            date: shift.date,
            startMin: shift.startMin,
            endMin: shift.endMin,
            breakMin: shift.breakMin,
            workers: shift.workers,
            po: shift.po,
          ),
      ];
  }

  void addShift(ShiftSlot slot) {
    draft.shifts.add(slot);
    notifyListeners();
  }

  void removeShift(String id) {
    draft.shifts.removeWhere((shift) => shift.id == id);
    notifyListeners();
  }

  ShiftSlot newSlot({
    required DateTime date,
    required int startMin,
    required int endMin,
    required int breakMin,
    required int workers,
    String? po,
  }) {
    return ShiftSlot(
      id: _id('shift'),
      date: dateOnly(date),
      startMin: startMin,
      endMin: endMin,
      breakMin: breakMin,
      workers: workers,
      po: po,
    );
  }

  void addContact(String name, String email) {
    final contact = SiteContact(id: _id('contact'), name: name, email: email);
    contacts.add(contact);
    draft.contactId = contact.id;
    notifyListeners();
  }

  void applyDraftToJob() {
    final id = editingJobId;
    if (id == null) return;
    final job = jobById(id);
    final site = worksiteById(draft.locationId) ?? worksites.first;
    final contact = contactById(draft.contactId) ?? contacts.first;
    if (job == null) return;
    job
      ..name = draft.name.trim().isEmpty ? job.name : draft.name.trim()
      ..roles = [...draft.roles]
      ..summary = draft.summary
      ..safety = draft.safety
      ..wage = draft.wage
      ..locationName = site.name
      ..locationAddress = site.address
      ..dressId = draft.dressId.isEmpty ? job.dressId : draft.dressId
      ..clothingNote = draft.clothingNote
      ..autoHire = draft.autoHire
      ..pool = draft.pool
      ..contactName = contact.name
      ..contactEmail = contact.email
      ..shifts = [
        for (final shift in draft.shifts) shift.copy(),
      ];
    editingJobId = null;
    notifyListeners();
  }

  String publish() {
    final site = worksiteById(draft.locationId) ?? worksites.first;
    final contact = contactById(draft.contactId) ?? contacts.first;
    final job = StaffJob(
      id: _id('job'),
      publicId: '602${DateTime.now().millisecondsSinceEpoch % 100000000}',
      name: draft.name.trim().isEmpty ? draft.roles.join(', ') : draft.name.trim(),
      roles: [...draft.roles],
      summary: draft.summary,
      safety: draft.safety,
      wage: draft.wage,
      locationName: site.name,
      locationAddress: site.address,
      dressId: draft.dressId.isEmpty ? 'smart-casual' : draft.dressId,
      clothingNote: draft.clothingNote,
      shifts: [for (final shift in draft.shifts) shift.copy()],
      applicants: [],
      autoHire: draft.autoHire,
      pool: draft.pool,
      contactName: contact.name,
      contactEmail: contact.email,
    );
    jobs.insert(0, job);
    editingJobId = null;
    notifyListeners();
    return job.id;
  }

  void hire(String jobId, String applicantId) {
    final job = jobById(jobId);
    if (job == null || job.hired >= job.needed) return;
    for (final applicant in job.applicants) {
      if (applicant.id == applicantId && applicant.status == 'applied') {
        applicant.status = 'hired';
      }
    }
    notifyListeners();
  }

  void decline(String jobId, String applicantId) {
    final job = jobById(jobId);
    if (job == null) return;
    for (final applicant in job.applicants) {
      if (applicant.id == applicantId && applicant.status == 'applied') {
        applicant.status = 'declined';
      }
    }
    notifyListeners();
  }

  void toggleSaved(String jobId, String applicantId) {
    final job = jobById(jobId);
    if (job == null) return;
    for (final applicant in job.applicants) {
      if (applicant.id == applicantId) applicant.saved = !applicant.saved;
    }
    notifyListeners();
  }

  void setAutoHire(String jobId, bool enabled, String pool) {
    final job = jobById(jobId);
    if (job == null) return;
    job
      ..autoHire = enabled
      ..pool = pool;
    notifyListeners();
  }

  void stopHiring(String jobId) {
    final job = jobById(jobId);
    if (job == null) return;
    job.status = 'stopped';
    for (final applicant in job.applicants) {
      if (applicant.status == 'applied') applicant.status = 'declined';
    }
    notifyListeners();
  }

  void approveHour(String id) {
    for (final entry in hours) {
      if (entry.id == id) entry.approved = true;
    }
    notifyListeners();
  }

  void approvePending() {
    for (final entry in hours) {
      entry.approved = true;
    }
    notifyListeners();
  }

  List<StaffJob> shiftsOn(DateTime day) {
    return [
      for (final job in jobs)
        if (job.open && job.shifts.any((shift) => sameDay(shift.date, day))) job,
    ];
  }

  void _seed() {
    contacts.addAll([
      SiteContact(
        id: 'contact-maya',
        name: 'Maya Steiner',
        email: 'maya.steiner@ayanlab.example',
      ),
      SiteContact(
        id: 'contact-jonas',
        name: 'Jonas Keller',
        email: 'jonas.keller@ayanlab.example',
      ),
    ]);

    final today = dateOnly(DateTime.now());
    final tomorrow = today.add(const Duration(days: 1));
    final later = today.add(const Duration(days: 2));
    final after = today.add(const Duration(days: 3));

    jobs.addAll([
      StaffJob(
        id: 'job-bar',
        publicId: '60240264005',
        name: 'Evening bar',
        roles: const ['Barback'],
        summary: 'Reset the bar, restock ice and glassware, and support service.',
        safety: 'Wet floors, glass, and late finish. Non-slip shoes required.',
        wage: 32,
        locationName: worksites.first.name,
        locationAddress: worksites.first.address,
        dressId: 'smart-casual',
        clothingNote: 'Black trousers and a plain black t-shirt. Apron is on site.',
        shifts: [
          ShiftSlot(
            id: 'shift-bar',
            date: tomorrow,
            startMin: 16 * 60,
            endMin: 23 * 60,
            breakMin: 30,
            workers: 1,
          ),
        ],
        applicants: _barApplicants(),
        autoHire: false,
        pool: 'trusted',
        contactName: 'Maya Steiner',
        contactEmail: 'maya.steiner@ayanlab.example',
      ),
      StaffJob(
        id: 'job-host',
        publicId: '60240264011',
        name: 'Host stand',
        roles: const ['Floor host'],
        summary: 'Meet guests and keep the book accurate through dinner.',
        safety: 'Standing work for the full shift.',
        wage: 33,
        locationName: worksites[1].name,
        locationAddress: worksites[1].address,
        dressId: 'office',
        clothingNote: '',
        shifts: [
          ShiftSlot(
            id: 'shift-host',
            date: later,
            startMin: 11 * 60,
            endMin: 18 * 60,
            breakMin: 30,
            workers: 2,
          ),
        ],
        applicants: [],
        autoHire: false,
        pool: 'trusted',
        contactName: 'Jonas Keller',
        contactEmail: 'jonas.keller@ayanlab.example',
      ),
      StaffJob(
        id: 'job-breakfast',
        publicId: '60240264018',
        name: 'Breakfast service',
        roles: const ['Waiter / Waitress'],
        summary: 'Breakfast plates, coffee, and a clean reset before lunch.',
        safety: 'Hot drinks and a busy pass.',
        wage: 33,
        locationName: worksites.first.name,
        locationAddress: worksites.first.address,
        dressId: 'provided',
        clothingNote: '',
        shifts: [
          ShiftSlot(
            id: 'shift-breakfast',
            date: after,
            startMin: 6 * 60 + 30,
            endMin: 12 * 60,
            breakMin: 0,
            workers: 2,
          ),
        ],
        applicants: [
          _person(
            name: 'Elena Vogt',
            role: 'Waiter / Waitress',
            rating: 4.9,
            reviews: 80,
            status: 'hired',
          ),
          _person(
            name: 'Samir Costa',
            role: 'Waiter / Waitress',
            rating: 4.7,
            reviews: 41,
            status: 'hired',
          ),
        ],
        autoHire: true,
        pool: 'trusted',
        contactName: 'Maya Steiner',
        contactEmail: 'maya.steiner@ayanlab.example',
      ),
    ]);

    hours.addAll([
      HourEntry(
        id: 'hour-1',
        worker: 'Elena Vogt',
        role: 'Breakfast service',
        date: today.subtract(const Duration(days: 2)),
        time: '06:30 – 12:00',
        hours: 5.5,
        approved: true,
      ),
      HourEntry(
        id: 'hour-2',
        worker: 'Samir Costa',
        role: 'Breakfast service',
        date: today.subtract(const Duration(days: 2)),
        time: '06:30 – 12:00',
        hours: 5.5,
        approved: true,
      ),
      HourEntry(
        id: 'hour-3',
        worker: 'Lena Meier',
        role: 'Evening bar',
        date: today.subtract(const Duration(days: 1)),
        time: '16:00 – 23:00',
        hours: 6.5,
      ),
      HourEntry(
        id: 'hour-4',
        worker: 'Omar Haddad',
        role: 'Banquet server',
        date: today.subtract(const Duration(days: 1)),
        time: '17:00 – 23:30',
        hours: 6,
      ),
    ]);

    startBlank(notify: false);
  }

  List<Applicant> _barApplicants() => [
        _person(
          name: 'Lena Meier',
          role: 'Barback',
          rating: 4.8,
          reviews: 65,
        ),
        _person(
          name: 'Omar Haddad',
          role: 'Barback',
          rating: 4.6,
          reviews: 120,
        ),
        _person(
          name: 'Sofia Keller',
          role: 'Bartender',
          rating: 4.4,
          reviews: 39,
        ),
        _person(
          name: 'Jonas Weber',
          role: 'Barback',
          rating: 4.5,
          reviews: 12,
        ),
      ];

  Applicant _person({
    required String name,
    required String role,
    required double rating,
    required int reviews,
    String status = 'applied',
  }) {
    final slug = name.toLowerCase().replaceAll(' ', '.');
    return Applicant(
      id: _id('person'),
      name: name,
      role: role,
      rating: rating,
      reviews: reviews,
      wageLabel: '${money(32)}/h',
      favouriteOf: 6 + reviews % 9,
      travel: '${20 + reviews % 25} min away',
      email: '$slug@example.com',
      phone: '+41 79 000 00 0${reviews % 9}',
      area: '8005 Zurich',
      about:
          'Hospitality professional used to busy rooms. Punctual, tidy, and comfortable on a team that calls the service out loud.',
      softSkills: const [
        ('Hard working', 4),
        ('Friendly', 3),
        ('Team work', 3),
        ('Clear communicator', 2),
      ],
      skills: const [
        ('Banquet service', 2),
        ('Bar setup', 3),
        ('Drinks service', 4),
      ],
      languages: const [
        ('German', 'C1 (advanced)', 'C1 (advanced)'),
        ('English', 'C1 (advanced)', 'B2 (upper intermediate)'),
        ('French', 'B1 (intermediate)', 'A2 (elementary)'),
      ],
      history: const [
        ('12 Nov 2026', 'Ayan Brasserie', 'Waiter / Waitress'),
        ('4 Nov 2026', 'Sihl Events', 'Banquet server'),
        ('28 Oct 2026', 'Limmat Kitchen', 'Runner'),
        ('19 Oct 2026', 'Ayan Kitchen', 'Barback'),
      ],
      documents: const ['Food hygiene certificate', 'Reference letter'],
      status: status,
    );
  }
}
