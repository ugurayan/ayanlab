import 'package:flutter/material.dart';

import 'staffing_theme.dart';

class FlowPage extends StatelessWidget {
  const FlowPage({
    super.key,
    required this.title,
    required this.body,
    this.onClose,
    this.closeIcon = false,
    this.actions = const [],
    this.footer,
  });

  final String title;
  final Widget body;
  final VoidCallback? onClose;
  final bool closeIcon;
  final List<Widget> actions;
  final Widget? footer;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: S.bg(context),
      body: Column(
        children: [
          SafeArea(
            bottom: false,
            child: SizedBox(
              height: 52,
              child: Row(
                children: [
                  SizedBox(
                    width: 56,
                    child: IconButton(
                      onPressed: onClose ?? () => Navigator.of(context).maybePop(),
                      icon: Icon(closeIcon ? Icons.close : Icons.arrow_back),
                      color: S.ink(context),
                    ),
                  ),
                  Expanded(
                    child: Text(
                      title,
                      textAlign: TextAlign.center,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: S.title(context),
                    ),
                  ),
                  SizedBox(
                    width: actions.isEmpty ? 56 : null,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.end,
                      mainAxisSize: MainAxisSize.min,
                      children: actions,
                    ),
                  ),
                ],
              ),
            ),
          ),
          Expanded(child: body),
          if (footer != null) footer!,
        ],
      ),
    );
  }
}

class StaffFooter extends StatelessWidget {
  const StaffFooter({
    super.key,
    required this.label,
    required this.onPressed,
    this.filled = false,
    this.caption,
    this.secondary,
  });

  final String label;
  final VoidCallback? onPressed;
  final bool filled;
  final String? caption;
  final Widget? secondary;

  @override
  Widget build(BuildContext context) {
    final enabled = onPressed != null;
    final bg = !enabled
        ? S.field(context)
        : filled
            ? S.accent
            : S.field(context);
    final fg = !enabled
        ? S.muted(context)
        : filled
            ? Colors.white
            : S.accent;
    return Material(
      color: S.bg(context),
      child: Container(
        decoration: BoxDecoration(
          border: Border(top: BorderSide(color: S.line(context))),
        ),
        padding: EdgeInsets.fromLTRB(16, 12, 16, 12 + MediaQuery.paddingOf(context).bottom),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (caption != null) ...[
              Text(caption!, style: S.body(context)),
              const SizedBox(height: 10),
            ],
            if (secondary != null) ...[
              secondary!,
              const SizedBox(height: 8),
            ],
            SizedBox(
              width: double.infinity,
              height: 52,
              child: TextButton(
                onPressed: onPressed,
                style: TextButton.styleFrom(
                  backgroundColor: bg,
                  foregroundColor: fg,
                  disabledBackgroundColor: bg,
                  disabledForegroundColor: fg,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                    side: filled
                        ? BorderSide.none
                        : BorderSide(color: S.line(context)),
                  ),
                ),
                child: Text(
                  label,
                  style: TextStyle(
                    color: fg,
                    fontWeight: FontWeight.w700,
                    letterSpacing: 0.6,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class Hairline extends StatelessWidget {
  const Hairline({super.key});

  @override
  Widget build(BuildContext context) {
    return Divider(height: 1, thickness: 1, color: S.line(context));
  }
}

class SearchBox extends StatelessWidget {
  const SearchBox({super.key, required this.onChanged, this.hint = 'Search'});

  final ValueChanged<String> onChanged;
  final String hint;

  @override
  Widget build(BuildContext context) {
    return TextField(
      onChanged: onChanged,
      style: S.body(context),
      decoration: InputDecoration(
        hintText: hint,
        hintStyle: TextStyle(color: S.muted(context)),
        prefixIcon: Icon(Icons.search, color: S.muted(context)),
        filled: true,
        fillColor: S.field(context),
        isDense: true,
        contentPadding: const EdgeInsets.symmetric(vertical: 12),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide.none,
        ),
      ),
    );
  }
}

class SelectChip extends StatelessWidget {
  const SelectChip({
    super.key,
    required this.label,
    required this.selected,
    required this.onTap,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: selected ? S.accent.withValues(alpha: 0.12) : S.chip(context),
      borderRadius: BorderRadius.circular(20),
      child: InkWell(
        borderRadius: BorderRadius.circular(20),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          child: Text(
            label,
            style: TextStyle(
              color: selected ? S.accent : S.ink(context),
              fontSize: 14,
              fontWeight: selected ? FontWeight.w600 : FontWeight.w500,
            ),
          ),
        ),
      ),
    );
  }
}

class AddRow extends StatelessWidget {
  const AddRow({super.key, required this.label, required this.onTap});

  final String label;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 12),
        child: Row(
          children: [
            Icon(Icons.add_circle_outline, color: S.accent, size: 22),
            const SizedBox(width: 8),
            Text(
              label,
              style: const TextStyle(
                color: S.accent,
                fontWeight: FontWeight.w600,
                fontSize: 16,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class RadioDot extends StatelessWidget {
  const RadioDot({super.key, required this.selected, this.active = true});

  final bool selected;
  final bool active;

  @override
  Widget build(BuildContext context) {
    final color = selected
        ? (active ? S.green : S.accent)
        : S.line(context);
    return Container(
      width: 22,
      height: 22,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: selected ? color : Colors.transparent,
        border: Border.all(color: color, width: 2),
      ),
      child: selected
          ? const Icon(Icons.circle, size: 8, color: Colors.white)
          : null,
    );
  }
}

class CheckBox extends StatelessWidget {
  const CheckBox({super.key, required this.selected});

  final bool selected;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 22,
      height: 22,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(4),
        color: selected ? S.accent : Colors.transparent,
        border: Border.all(color: selected ? S.accent : S.muted(context)),
      ),
      child: selected
          ? const Icon(Icons.check, size: 16, color: Colors.white)
          : null,
    );
  }
}

class StaffSwitch extends StatelessWidget {
  const StaffSwitch({super.key, required this.value, required this.onChanged});

  final bool value;
  final ValueChanged<bool> onChanged;

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: value,
      onChanged: onChanged,
      trackColor: WidgetStateProperty.resolveWith((states) {
        if (states.contains(WidgetState.selected)) return S.green;
        return S.field(context);
      }),
    );
  }
}

Future<T?> showStaffSheet<T>(BuildContext context, Widget child) {
  return showModalBottomSheet<T>(
    context: context,
    backgroundColor: S.bg(context),
    showDragHandle: true,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
    ),
    builder: (context) => SafeArea(
      child: Padding(
        padding: const EdgeInsets.only(bottom: 8),
        child: child,
      ),
    ),
  );
}

class SheetAction extends StatelessWidget {
  const SheetAction({super.key, required this.label, required this.onTap, this.danger = false});

  final String label;
  final VoidCallback onTap;
  final bool danger;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(
        label,
        style: TextStyle(
          fontSize: 17,
          color: danger ? S.danger : S.ink(context),
        ),
      ),
      onTap: onTap,
    );
  }
}

class IconThumb extends StatelessWidget {
  const IconThumb({super.key, required this.icon});

  final IconData icon;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 64,
      height: 64,
      alignment: Alignment.center,
      decoration: BoxDecoration(
        color: S.field(context),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Icon(icon, color: S.ink(context)),
    );
  }
}

class Initials extends StatelessWidget {
  const Initials({super.key, required this.name, this.radius = 28});

  final String name;
  final double radius;

  @override
  Widget build(BuildContext context) {
    final parts = name.split(' ').where((part) => part.isNotEmpty).toList();
    final letters = parts.take(2).map((part) => part[0]).join().toUpperCase();
    final hues = [S.accent, const Color(0xFF2563EB), const Color(0xFF0F766E), const Color(0xFFB45309)];
    final color = hues[name.codeUnits.fold<int>(0, (a, b) => a + b) % hues.length];
    return CircleAvatar(
      radius: radius,
      backgroundColor: color.withValues(alpha: 0.15),
      child: Text(
        letters,
        style: TextStyle(
          color: color,
          fontWeight: FontWeight.w700,
          fontSize: radius * 0.62,
        ),
      ),
    );
  }
}

class StarRow extends StatelessWidget {
  const StarRow({super.key, required this.rating, this.size = 16});

  final double rating;
  final double size;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        for (var i = 1; i <= 5; i++)
          Icon(
            rating >= i
                ? Icons.star
                : rating >= i - 0.5
                    ? Icons.star_half
                    : Icons.star_border,
            size: size,
            color: S.accent,
          ),
      ],
    );
  }
}
