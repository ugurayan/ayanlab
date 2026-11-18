import React, { useRef, useEffect, useCallback } from 'react';
import { Bold, Italic, Underline, List, ListOrdered, Type } from 'lucide-react';

const ALLOWED_TAGS = /^(p|br|strong|b|em|i|u|h[1-4]|ul|ol|li|a|span|div)$/i;
const ALLOWED_ATTRS: Record<string, string[]> = { a: ['href', 'target', 'rel'] };

export function sanitizeHtml(html: string): string {
  if (!html || typeof html !== 'string') return '';
  const doc = new DOMParser().parseFromString(html, 'text/html');
  const walk = (node: Node): string => {
    if (node.nodeType === Node.TEXT_NODE) return node.textContent || '';
    if (node.nodeType !== Node.ELEMENT_NODE) return '';
    const el = node as Element;
    const tag = el.tagName.toLowerCase();
    if (!ALLOWED_TAGS.test(tag)) return Array.from(node.childNodes).map(walk).join('');
    const attrs: string[] = [];
    if (ALLOWED_ATTRS[tag]) {
      ALLOWED_ATTRS[tag].forEach((a) => {
        const v = el.getAttribute(a);
        if (v) attrs.push(`${a}="${v.replace(/"/g, '&quot;')}"`);
      });
    }
    const inner = Array.from(node.childNodes).map(walk).join('');
    if (tag === 'br') return '<br>';
    return `<${tag}${attrs.length ? ' ' + attrs.join(' ') : ''}>${inner}</${tag}>`;
  };
  const body = doc.body;
  return Array.from(body.childNodes).map(walk).join('').trim() || '';
}

interface SimpleRichEditorProps {
  value: string;
  onChange: (html: string) => void;
  placeholder?: string;
  minHeight?: string;
  className?: string;
}

export function SimpleRichEditor({
  value,
  onChange,
  placeholder = 'Start typing...',
  minHeight = '200px',
  className = '',
}: SimpleRichEditorProps) {
  const divRef = useRef<HTMLDivElement>(null);
  const isInternalChange = useRef(false);

  useEffect(() => {
    const el = divRef.current;
    if (!el) return;
    const current = el.innerHTML;
    const normalized = value || '';
    if (normalized === '' && current === '') return;
    if (isInternalChange.current) {
      isInternalChange.current = false;
      return;
    }
    if (current !== normalized) {
      el.innerHTML = normalized || '';
    }
  }, [value]);

  const report = useCallback(() => {
    const el = divRef.current;
    if (!el) return;
    const html = el.innerHTML.trim();
    const safe = sanitizeHtml(html);
    isInternalChange.current = true;
    onChange(safe);
  }, [onChange]);

  const exec = useCallback(
    (command: string, value?: string) => {
      document.execCommand(command, false, value);
      divRef.current?.focus();
      report();
    },
    [report]
  );

  return (
    <div className={`rounded-xl border border-slate-200 overflow-hidden bg-white ${className}`}>
      <div className="flex flex-wrap items-center gap-1 p-2 border-b border-slate-200 bg-slate-50">
        <button type="button" onClick={() => exec('bold')} className="p-2 rounded hover:bg-slate-200" title="Bold">
          <Bold className="w-4 h-4 text-slate-700" />
        </button>
        <button type="button" onClick={() => exec('italic')} className="p-2 rounded hover:bg-slate-200" title="Italic">
          <Italic className="w-4 h-4 text-slate-700" />
        </button>
        <button type="button" onClick={() => exec('underline')} className="p-2 rounded hover:bg-slate-200" title="Underline">
          <Underline className="w-4 h-4 text-slate-700" />
        </button>
        <span className="w-px h-5 bg-slate-300 mx-1" />
        <button type="button" onClick={() => exec('formatBlock', 'h1')} className="p-2 rounded hover:bg-slate-200 text-xs font-bold" title="Heading 1">
          H1
        </button>
        <button type="button" onClick={() => exec('formatBlock', 'h2')} className="p-2 rounded hover:bg-slate-200 text-xs font-bold" title="Heading 2">
          H2
        </button>
        <button type="button" onClick={() => exec('formatBlock', 'h3')} className="p-2 rounded hover:bg-slate-200 text-xs font-bold" title="Heading 3">
          H3
        </button>
        <button type="button" onClick={() => exec('formatBlock', 'h4')} className="p-2 rounded hover:bg-slate-200 text-xs font-bold" title="Heading 4">
          H4
        </button>
        <button type="button" onClick={() => exec('formatBlock', 'p')} className="p-2 rounded hover:bg-slate-200" title="Paragraph">
          <Type className="w-4 h-4 text-slate-700" />
        </button>
        <span className="w-px h-5 bg-slate-300 mx-1" />
        <button type="button" onClick={() => exec('insertUnorderedList')} className="p-2 rounded hover:bg-slate-200" title="Bullet list">
          <List className="w-4 h-4 text-slate-700" />
        </button>
        <button type="button" onClick={() => exec('insertOrderedList')} className="p-2 rounded hover:bg-slate-200" title="Numbered list">
          <ListOrdered className="w-4 h-4 text-slate-700" />
        </button>
      </div>
      <div
        ref={divRef}
        contentEditable
        className="simple-rich-editor min-w-0 p-3 text-sm text-slate-800 focus:outline-none prose prose-sm max-w-none prose-p:my-1 prose-ul:my-1 prose-ol:my-1 prose-li:my-0 prose-h1:text-base prose-h2:text-sm prose-h3:text-sm prose-h4:text-sm"
        style={{ minHeight }}
        data-placeholder={placeholder}
        onInput={report}
        onBlur={report}
        suppressContentEditableWarning
      />
      <style>{`
        .simple-rich-editor:empty:before { content: attr(data-placeholder); color: #94a3b8; }
        .simple-rich-editor h1 { font-size: 1.25rem; font-weight: 700; margin: 0.5em 0; }
        .simple-rich-editor h2 { font-size: 1.125rem; font-weight: 600; margin: 0.5em 0; }
        .simple-rich-editor h3 { font-size: 1rem; font-weight: 600; margin: 0.25em 0; }
        .simple-rich-editor h4 { font-size: 0.875rem; font-weight: 600; margin: 0.25em 0; }
        .simple-rich-editor ul {
          list-style-type: disc !important;
          list-style-position: outside;
          padding-left: 1.5rem;
          margin: 0.25em 0;
        }
        .simple-rich-editor ol {
          list-style-type: decimal !important;
          list-style-position: outside;
          padding-left: 1.5rem;
          margin: 0.25em 0;
        }
        .simple-rich-editor li {
          display: list-item !important;
          margin: 0.125em 0;
        }
      `}</style>
    </div>
  );
}
