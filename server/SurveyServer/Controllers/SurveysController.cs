using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using SurveyServer.Data;
using SurveyServer.Models;
using SurveyServer.Scripts;

namespace SurveyServer.Controllers
{
    public class SurveysController : Controller
    {
        private readonly ApplicationDbContext _context;

        public SurveysController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: Surveys
        public async Task<IActionResult> Index()
        {
            return View(await _context.Survey.ToListAsync());
        }

        // GET: Surveys/Details/5
        public async Task<IActionResult> Details(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var survey = await _context.Survey
                .FirstOrDefaultAsync(m => m.SurveyID == id);
            if (survey == null)
            {
                return NotFound();
            }
            List<Question> questions = await _context.Question
                .Include(q => q.Survey)
                .Where(m=>m.RefSurveyID==id)
                .ToListAsync();
            survey.Questions = questions;
            return View(survey);
        }

        // GET: Surveys/Create
        public IActionResult Create()
        {
            ViewData["SurveyID"] = RandomStringBuilder.Create();
            
            return View();
        }

        private bool IsUnique(string str)
        {
            if (Regex.IsMatch(str, @"^(?!\d*?(\d)\d*?\1)\d{0,9}$")&&!str.Contains("9"))
            {
                return true;
            }
            return false;
        }
        // POST: Surveys/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create([Bind("SurveyID,SurveyTitle,PatternLock")] Survey survey)
        {
            if (ModelState.IsValid)
            {
                if (!IsUnique(survey.PatternLock))
                {
                    ViewData["SurveyID"] = survey.SurveyID;
                    return View(survey);
                }
                _context.Add(survey);
                await _context.SaveChangesAsync();
                return RedirectToAction(nameof(Index));
            }
            ViewData["SurveyID"] = survey.SurveyID;
            return View(survey);
        }

        // GET: Surveys/Edit/5
        public async Task<IActionResult> Edit(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var survey = await _context.Survey.FindAsync(id);
            if (survey == null)
            {
                return NotFound();
            }
            return View(survey);
        }

        // POST: Surveys/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(string id, [Bind("SurveyID,SurveyTitle,PatternLock")] Survey survey)
        {
            if (id != survey.SurveyID)
            {
                return NotFound();
            }

            if (ModelState.IsValid)
            {
                if (!IsUnique(survey.PatternLock))
                {
                    return View(survey);
                }
                try
                {
                    _context.Update(survey);
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateConcurrencyException)
                {
                    if (!SurveyExists(survey.SurveyID))
                    {
                        return NotFound();
                    }
                    else
                    {
                        throw;
                    }
                }
                return RedirectToAction(nameof(Index));
            }
            return View(survey);
        }

        // GET: Surveys/Delete/5
        public async Task<IActionResult> Delete(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var survey = await _context.Survey
                .FirstOrDefaultAsync(m => m.SurveyID == id);
            if (survey == null)
            {
                return NotFound();
            }

            return View(survey);
        }

        // POST: Surveys/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(string id)
        {
            var responses = _context.Response.Include(s => s.Survey).Where(m => m.RefSurveyID == id);
            if (responses!=null)
            {
                foreach (Response response in responses)
                {
                    string responseid = response.ResponseID;
                    var answers = _context.Answer.Include(r => r.Response).Where(m => m.RefResponseID == responseid);
                    if (answers != null)
                    {
                        foreach (Answer answer in answers)
                        {
                            _context.Answer.Remove(answer);
                        }
                        await _context.SaveChangesAsync();
                    }
                    _context.Response.Remove(response);
                    await _context.SaveChangesAsync();
                }
            }
            var questions = _context.Question.Include(s => s.Survey).Where(m => m.RefSurveyID == id);
            if (questions != null)
            {
                foreach (Question question in questions)
                {
                    _context.Question.Remove(question);
                }
                await _context.SaveChangesAsync();
            }
            await _context.SaveChangesAsync();
            var survey = await _context.Survey.FindAsync(id);
            _context.Survey.Remove(survey);
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        private bool SurveyExists(string id)
        {
            return _context.Survey.Any(e => e.SurveyID == id);
        }
    }
}
